package com.guillaume.myapplication.ui.chat;

import android.graphics.drawable.GradientDrawable;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.guillaume.myapplication.R;
import com.guillaume.myapplication.model.firestore.MessageFirebase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    //private ItemChatBinding binding;

    private final TextView messageTextView;
    private final TextView dateTextView;
    private final ImageView profileImage;
    private final ImageView senderImageView;
    private final LinearLayout messageTextContainer;
    private final LinearLayout profileContainer;
    private final LinearLayout messageContainer;

    private final int colorCurrentUser;
    private final int colorRemoteUser;

    private boolean isSender;

    public MessageViewHolder(@NonNull View itemView, boolean isSender) {
        super(itemView);
        this.isSender = isSender;
        //binding = ItemChatBinding.bind(itemView);

        messageTextView = itemView.findViewById(R.id.messageTextView);
        dateTextView = itemView.findViewById(R.id.dateTextView);
        profileImage = itemView.findViewById(R.id.profileImage);
        senderImageView = itemView.findViewById(R.id.senderImageView);
        messageTextContainer = itemView.findViewById(R.id.messageTextContainer);
        profileContainer = itemView.findViewById(R.id.profileContainer);
        messageContainer = itemView.findViewById(R.id.messageContainer);


        // Setup default colors
        colorCurrentUser = ContextCompat.getColor(itemView.getContext(), R.color.orange);
        colorRemoteUser = ContextCompat.getColor(itemView.getContext(), R.color.blue);
    }

    public void updateWithMessage(MessageFirebase message, RequestManager glide) {

        // Update message
        //binding.messageTextView.setText(message.getMessage());
        //binding.messageTextView.setTextAlignment(isSender ? View.TEXT_ALIGNMENT_TEXT_END : View.TEXT_ALIGNMENT_TEXT_START);
        messageTextView.setText(message.getMessage());
        messageTextView.setTextAlignment(isSender ? View.TEXT_ALIGNMENT_TEXT_END : View.TEXT_ALIGNMENT_TEXT_START);
        // Update date
        //if (message.getDateCreated() != null) binding.dateTextView.setText(this.convertDateToHour(message.getDateCreated()));
        if (message.getDateCreated() != null){
            dateTextView.setText(this.convertDateToHour(message.getDateCreated()));}


        // Update profile picture
        if (message.getUserSender().getUrlPicture() != null){
            glide.load(message.getUserSender().getUrlPicture())
                    .apply(RequestOptions.circleCropTransform())
                    //.into(binding.profileImage);
                    .into(profileImage);}

        // Update image sent
        if (message.getUrlImage() != null) {
            glide.load(message.getUrlImage())
                    //.into(binding.senderImageView);
                    .into(senderImageView);
            //binding.senderImageView.setVisibility(View.VISIBLE);
            senderImageView.setVisibility(View.VISIBLE);
        } else {
            //binding.senderImageView.setVisibility(View.GONE);
            senderImageView.setVisibility(View.GONE);
        }

        updateLayoutFromSenderType();
    }

    private void updateLayoutFromSenderType() {

        //Update Message Bubble Color Background
        /*((GradientDrawable) binding.messageTextContainer.getBackground()).setColor(isSender ? colorCurrentUser : colorRemoteUser);
        binding.messageTextContainer.requestLayout();*/
        //todo to test
        ((GradientDrawable) messageTextContainer.getBackground()).setColor(isSender ? colorCurrentUser : colorRemoteUser);
        messageTextContainer.requestLayout();

        if (!isSender) {
            updateProfileContainer();
            updateMessageContainer();
        }
    }

    private void updateProfileContainer() {
        // Update the constraint for the profile container (Push it to the left for receiver message)
        //ConstraintLayout.LayoutParams profileContainerLayoutParams = (ConstraintLayout.LayoutParams) binding.profileContainer.getLayoutParams();
        ConstraintLayout.LayoutParams profileContainerLayoutParams = (ConstraintLayout.LayoutParams) profileContainer.getLayoutParams();
        profileContainerLayoutParams.endToEnd = ConstraintLayout.LayoutParams.UNSET;
        profileContainerLayoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        //binding.profileContainer.requestLayout();
        profileContainer.requestLayout();
    }

    private void updateMessageContainer() {
        // Update the constraint for the message container (Push it to the right of the profile container for receiver message)
        //ConstraintLayout.LayoutParams messageContainerLayoutParams = (ConstraintLayout.LayoutParams) binding.messageContainer.getLayoutParams();
        ConstraintLayout.LayoutParams messageContainerLayoutParams = (ConstraintLayout.LayoutParams) messageContainer.getLayoutParams();
        messageContainerLayoutParams.startToStart = ConstraintLayout.LayoutParams.UNSET;
        messageContainerLayoutParams.endToStart = ConstraintLayout.LayoutParams.UNSET;
        //messageContainerLayoutParams.startToEnd = binding.profileContainer.getId();
        messageContainerLayoutParams.startToEnd = profileContainer.getId();
        messageContainerLayoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        messageContainerLayoutParams.horizontalBias = 0.0f;
        //binding.messageContainer.requestLayout();
        messageContainer.requestLayout();

        // Update the constraint (gravity) for the text of the message (content + date) (Align it to the left for receiver message)
        //LinearLayout.LayoutParams messageTextLayoutParams = (LinearLayout.LayoutParams) binding.messageTextContainer.getLayoutParams();
        LinearLayout.LayoutParams messageTextLayoutParams = (LinearLayout.LayoutParams) messageTextContainer.getLayoutParams();
        messageTextLayoutParams.gravity = Gravity.START;
       // binding.messageTextContainer.requestLayout();
        messageTextContainer.requestLayout();

        //LinearLayout.LayoutParams dateLayoutParams = (LinearLayout.LayoutParams) binding.dateTextView.getLayoutParams();
        LinearLayout.LayoutParams dateLayoutParams = (LinearLayout.LayoutParams) dateTextView.getLayoutParams();
        dateLayoutParams.gravity = Gravity.BOTTOM | Gravity.START;
        //binding.dateTextView.requestLayout();
        dateTextView.requestLayout();

    }

    private String convertDateToHour(Date date) {
        DateFormat dfTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return dfTime.format(date);
    }

}
