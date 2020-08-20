package com.example.uploadimage.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.uploadimage.Adapter.PhotosAdapter;
import com.example.uploadimage.EditProfileActivity;
import com.example.uploadimage.Model.Post;
import com.example.uploadimage.Model.User;
import com.example.uploadimage.R;
import com.example.uploadimage.StartActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProfileFragment extends Fragment
//        implements  PopupMenu.OnMenuItemClickListener
{

    ImageView image_profile;
    TextView posts, followers, following, fullname, bio, username, logout;
    Button edit_profile;

    RecyclerView recyclerView;
    PhotosAdapter photosAdapter;
    List<Post> postList;

    FirebaseUser firebaseUser;
    String profileID;

    ImageButton my_photos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileID = prefs.getString("profileid", "none");

        image_profile = view.findViewById(R.id.image_profile);
        logout = view.findViewById(R.id.logout);
        posts = view.findViewById(R.id.posts);
//        followers = view.findViewById(R.id.followers);
//        following = view.findViewById(R.id.following);
//        fullname = view.findViewById(R.id.fullname);
//        bio = view.findViewById(R.id.bio);
        username = view.findViewById(R.id.username);
        edit_profile = view.findViewById(R.id.edit_profile);
        //my_photos = view.findViewById(R.id.my_photos);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        photosAdapter = new PhotosAdapter(getContext(), postList);
        recyclerView.setAdapter(photosAdapter);

        //showPopUp(options);
        userInfo();
        //getFollowers();
        getNoOfPosts();
        myPhotos();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), StartActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//                Intent intent = new Intent (getContext(), OptionsActivity.class);
//                startActivity(intent);
            }
        });

        if(profileID.equals(firebaseUser.getUid())){
            edit_profile.setText("Edit Profile");
        } else{
            //checkFollow();
        }

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn = edit_profile.getText().toString();

                if(btn.equals("Edit Profile")){
                    startActivity(new Intent(getContext(), EditProfileActivity.class));
                } else if(btn.equals("follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following").child(profileID).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileID).child("followers").child(firebaseUser.getUid()).setValue(true);

                } else if(btn.equals("following")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following").child(profileID).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileID).child("followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        return view;

    }

    private void userInfo(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getContext() == null){
                    return;
                }

                User user = snapshot.getValue(User.class);
                if (user != null){
                    Glide.with(getContext()).load(user.getImageurl()).into(image_profile);
                    username.setText(user.getUsername());
//                    bio.setText(user.getBio());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getNoOfPosts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileID)){
                        i++;
                    }
                }
                posts.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void myPhotos(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileID)){
                        postList.add(post);
                    }
                }

                Collections.reverse(postList);
                photosAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}