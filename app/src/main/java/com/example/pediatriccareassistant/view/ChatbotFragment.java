package com.example.pediatriccareassistant.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pediatriccareassistant.R;
import com.example.pediatriccareassistant.controller.ChatbotController;
import com.example.pediatriccareassistant.model.callback.ChatbotLoadEmbeddingsCallback;
import com.example.pediatriccareassistant.databinding.FragmentChatbotBinding;

public class ChatbotFragment extends MainMenuBaseFragment
{
    private FragmentChatbotBinding binding;
    private RecyclerView messages_recycler_view;
    private ImageButton send_message_button;
    private EditText send_message_content;
    private LinearLayout loading_layout;
    private LinearLayout typing_layout;

    private ChatbotController chatbotController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        binding = FragmentChatbotBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        messages_recycler_view = root.findViewById(R.id.chatbot_messages_recycler_view);
        send_message_button = root.findViewById(R.id.chatbot_send_message_button);
        send_message_content = root.findViewById(R.id.chatbot_message_edit);
        loading_layout = root.findViewById(R.id.chatbot_loading_layout);
        typing_layout = root.findViewById(R.id.chatbot_typing_layout);

        initializeChatbotController(root);
        initializeSendMessageButton(root);

        return root;
    }

    private void initializeChatbotController(View root) {
        chatbotController = new ChatbotController(root.getContext(), messages_recycler_view, typing_layout, new ChatbotLoadEmbeddingsCallback() {
            @Override
            public void onStart() {
                loading_layout.setVisibility(View.VISIBLE);
                send_message_button.setEnabled(false);
            }

            @Override
            public void onSuccess() {
                loading_layout.setVisibility(View.GONE);
                send_message_button.setEnabled(true);
            }

            @Override
            public void onFailure() {
                loading_layout.setVisibility(View.GONE);
                send_message_button.setEnabled(false);
            }
        });
    }

    private void initializeSendMessageButton(View root) {
        send_message_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (send_message_content.getText().toString().isEmpty()) {
                    Toast.makeText(root.getContext(), "Please enter your message..", Toast.LENGTH_SHORT).show();
                    return;
                }
                chatbotController.sendMessage(root.getContext(), send_message_content.getText().toString());
                send_message_content.setText("");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
