package com.example.pediatriccareassistant.controller.firebasehandler;

import android.content.Context;
import android.os.AsyncTask;
import androidx.annotation.NonNull;

import com.example.pediatriccareassistant.R;
import com.example.pediatriccareassistant.model.callback.DataCallback;
import com.google.firebase.database.*;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import java.util.*;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

/**
 * Handles retrieval and creation of embeddings for symptoms and articles.
 */
public class EmbeddingsHandler extends BaseHandler {

    private static final EmbeddingsHandler instance = new EmbeddingsHandler();

    private EmbeddingsHandler() {
    }

    public static EmbeddingsHandler getInstance() {
        return instance;
    }

    /**
     * Retrieves embeddings from the specified Firebase path and returns them as a JSON string.
     *
     * @param embeddingsPath The Firebase path to retrieve embeddings from.
     * @param callback       The callback to handle the success or failure of the operation.
     */
    public void retrieveEmbeddings(String embeddingsPath, DataCallback<String> callback) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference(embeddingsPath);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Object object = snapshot.getValue(Object.class);
                String json = new Gson().toJson(object);
                callback.onSuccess(json);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.toException());
            }
        });
    }

    /**
     * Creates embeddings for symptoms and stores them in Firebase.
     *
     * @param context The application context.
     */
    public void createEmbeddings(Context context) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("symptoms");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                new CreateEmbeddingsTask(context, "symptoms_embeddings").execute(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
    }

    private static class CreateEmbeddingsTask extends AsyncTask<DataSnapshot, Void, String> {
        private final Context context;
        private final String firebasePath;
        private OpenAiEmbeddingModel embeddingModel;
        private InMemoryEmbeddingStore<TextSegment> embeddingStore;

        public CreateEmbeddingsTask(Context context, String firebasePath) {
            this.context = context;
            this.firebasePath = firebasePath;
        }

        @Override
        protected String doInBackground(DataSnapshot... snapshots) {
            List<Document> documents = new ArrayList<>();
            DataSnapshot snapshot = snapshots[0];
            embeddingModel = OpenAiEmbeddingModel.withApiKey(context.getString(R.string.openai_key));
            embeddingStore = new InMemoryEmbeddingStore<>();

            for (DataSnapshot symptomSnapshot : snapshot.getChildren()) {
                Metadata metadata = new Metadata();
                metadata.put("symptom_id", symptomSnapshot.child("id").getValue().toString());
                metadata.put("disease_name", symptomSnapshot.child("disease").getValue().toString());
                String content = removeSpecialChars(symptomSnapshot.child("symptom").getValue().toString());

                documents.add(Document.from(content, metadata));
            }

            processBatch(documents);

            return embeddingStore.serializeToJson();
        }

        private void processBatch(List<Document> batch) {
            EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                    .embeddingModel(embeddingModel)
                    .embeddingStore(embeddingStore)
                    .build();
            ingestor.ingest(batch);
        }

        private static String removeSpecialChars(String text) {
            if (text == null || text.isEmpty()) {
                return "";
            }
            return Jsoup.parse(text).text().replaceAll("[^a-zA-Z\\s]", "").toLowerCase();
        }

        @Override
        protected void onPostExecute(String jsonEmbeddings) {
            try {
                JSONObject jsonObject = new JSONObject(jsonEmbeddings);
                JSONArray entriesArray = jsonObject.getJSONArray("entries");
                DatabaseReference database = FirebaseDatabase.getInstance().getReference(firebasePath).child("entries");

                for (int i = 0; i < entriesArray.length(); i++) {
                    JSONObject entry = entriesArray.getJSONObject(i);
                    Map<String, Object> entryMap = jsonToMap(entry);
                    int finalI = i;
                    database.child(String.valueOf(i)).setValue(entryMap).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            System.out.println("Entry " + finalI + " saved successfully into Firebase");
                        } else {
                            System.out.println("Failed to save entry " + finalI + " into Firebase: " + task.getException());
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private Map<String, Object> jsonToMap(JSONObject jsonObject) throws JSONException {
            Map<String, Object> map = new HashMap<>();
            Iterator<String> keys = jsonObject.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                Object value = jsonObject.get(key);

                if (value instanceof JSONArray) {
                    value = jsonArrayToList((JSONArray) value);
                } else if (value instanceof JSONObject) {
                    value = jsonToMap((JSONObject) value);
                }

                map.put(key, value);
            }
            return map;
        }

        private List<Object> jsonArrayToList(JSONArray array) throws JSONException {
            List<Object> list = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                Object value = array.get(i);

                if (value instanceof JSONArray) {
                    value = jsonArrayToList((JSONArray) value);
                } else if (value instanceof JSONObject) {
                    value = jsonToMap((JSONObject) value);
                }

                list.add(value);
            }
            return list;
        }
    }
}
