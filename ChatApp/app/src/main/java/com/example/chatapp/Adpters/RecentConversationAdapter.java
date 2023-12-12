package com.example.chatapp.Adpters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.databinding.ItemContainerRecentCoversionBinding;
import com.example.chatapp.models.ChatMessage;

import java.util.List;

public class RecentConversationAdapter extends RecyclerView.Adapter<RecentConversationAdapter.ConversasionViewHilder> {

    private final List<ChatMessage> chatMessages;

    public RecentConversationAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }


    class ConversasionViewHilder extends RecyclerView.ViewHolder {
        ItemContainerRecentCoversionBinding binding;

        ConversasionViewHilder(ItemContainerRecentCoversionBinding itemContainerRecentCoversionBinding) {
            super(itemContainerRecentCoversionBinding.getRoot());
            binding = itemContainerRecentCoversionBinding;
        }

        void setdata(ChatMessage chatMessage) {
            binding.imagemProfile.setImageBitmap(getConversionImage(chatMessage.conversionImage));
            binding.textName.setText(chatMessage.conversionName);
            binding.textRecentMessage.setText(chatMessage.message);
        }
    }

    @NonNull
    @Override
    public ConversasionViewHilder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversasionViewHilder(
                ItemContainerRecentCoversionBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent, false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ConversasionViewHilder holder, int position) {
        holder.setdata(chatMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    //revisado
    private Bitmap getConversionImage(String encodeImage) {
        byte[] bytes = Base64.decode(encodeImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

    }
}
