package com.example.uploadimage.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.uploadimage.Fragment.PostDetailFragment;
import com.example.uploadimage.Fragment.ProfileFragment;
import com.example.uploadimage.MessageActivity;
import com.example.uploadimage.Model.User;
import com.example.uploadimage.Model.Post;
import com.example.uploadimage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{

    public Context mContext;
    public List<Post> mPost;

    private FirebaseUser firebaseUser;
    private FirebaseStorage mStorage =  FirebaseStorage.getInstance();

    public PostAdapter(Context mContext, List<Post> mPost){
        this.mContext = mContext;
        this.mPost = mPost;;

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Post post = mPost.get(position);

        if (post.getPostImage() != null) {
            Glide.with(mContext).load(post.getPostImage()).into(holder.post_image);
        }

        if (post.getDescription().equals("")){
            holder.description.setVisibility(View.GONE);

            holder.title.setVisibility(View.VISIBLE);
            holder.title.setText(post.getTitle());
        }
        else{
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getDescription());

            holder.title.setVisibility(View.VISIBLE);
            holder.title.setText(post.getTitle());
        }

        if(post.getDate()!= null && post.getDate().equals("Select date")){
            holder.date.setVisibility(View.GONE);
        } else{
            holder.date.setVisibility(View.VISIBLE);
            holder.date.setText(post.getDate());
        }


        publisherInfo(holder.image_profile, holder.username, post.getPublisher());
        isLiked(post.getPostId(), holder.like);
        noOfLikes(holder.likes, post.getPostId());

        holder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", post.getPublisher());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            }
        });

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", post.getPublisher());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            }
        });

        holder.publisher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", post.getPublisher());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            }
        });

        holder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("postid", post.getPostId());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PostDetailFragment()).commit();
            }
        });

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.like.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId()).child(firebaseUser.getUid()).setValue(firebaseUser.getUid());
                    FirebaseDatabase.getInstance().getReference().child("Connections").child(post.getPublisher()).child("connections").child("likes").child(firebaseUser.getUid()).setValue(true);
                    isMatched(post.getPublisher());
                }
                else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId()).child(firebaseUser.getUid()).removeValue();
                }
            }
        });


        if (!post.getPublisher().equals(firebaseUser.getUid())) {
            holder.delete.setVisibility(View.GONE);
        }
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog =  new AlertDialog.Builder(mContext)
                        .setTitle("Delete Memory")
                        .setMessage("Are you sure you want to delete this memory?")
                        .setPositiveButton("Yes", null)
                        .setNegativeButton("Cancel", null)
                        .show();

                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseDatabase.getInstance().getReference("Posts")
                                .child(post.getPostId()).removeValue();

                        StorageReference photoRef = mStorage.getReferenceFromUrl(post.getPostImage());
                        photoRef.delete();
                        Toast.makeText(mContext, "Deleted", Toast.LENGTH_SHORT).show();

                    }
                });

                }
        });
    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView image_profile, post_image, like, delete;
        public TextView username, likes, publisher, title, date, description;

        public ViewHolder(@NonNull View itemView){
            super (itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            post_image = itemView.findViewById(R.id.post_image);
            like = itemView.findViewById(R.id.like);
            username = itemView.findViewById(R.id.username);
            likes = itemView.findViewById(R.id.likes);
            publisher = itemView.findViewById(R.id.publisher);
            title = itemView.findViewById(R.id.title);
            date = itemView.findViewById(R.id.date);
            description = itemView.findViewById(R.id.description);
            delete = itemView.findViewById(R.id.delete);
        }

    }

    private void isLiked(String postID, final ImageView imageView){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(postID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_like_red);
                    imageView.setTag("liked");
                } else{
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void isMatched(final String publisher){

        final DatabaseReference userConnectionsDB = FirebaseDatabase.getInstance().getReference("Connections").child(firebaseUser.getUid()).child("connections").child("likes").child(publisher);
        final DatabaseReference  usersDB = FirebaseDatabase.getInstance().getReference("Connections");
        final DatabaseReference matchesDB = FirebaseDatabase.getInstance().getReference("Connections").child(publisher).child("connections").child("matches");

        userConnectionsDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    usersDB.child(snapshot.getKey()).child("connections").child("matches").child(firebaseUser.getUid()).setValue(true);
                    usersDB.child(firebaseUser.getUid()).child("connections").child("matches").child(snapshot.getKey()).setValue(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        matchesDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    FirebaseDatabase.getInstance().getReference("Matches").child(firebaseUser.getUid()).child(publisher).setValue(publisher);
                    Intent intent = new Intent (mContext, MessageActivity.class);
                    mContext.startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }



    private void noOfLikes(final TextView likes, String postID){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likes.setText(snapshot.getChildrenCount()+ " likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

        private void publisherInfo(final ImageView image_profile, final TextView username, final String userId){

            DatabaseReference oppositeNationDB = FirebaseDatabase.getInstance().getReference("Users").child(userId);
            DatabaseReference currentUsersNationDB = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

            final String[] usersNation = new String[1];
            currentUsersNationDB.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String currentNation = snapshot.child("nation").getValue().toString();

                    usersNation[0] = currentNation;

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            oppositeNationDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userObj = snapshot.getValue(User.class);

                    if ((!userObj.getNation().equals(usersNation[0]))) {
                        Glide.with(mContext).load(userObj.getImageurl()).into(image_profile);
                        username.setText(userObj.getUsername());

                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
