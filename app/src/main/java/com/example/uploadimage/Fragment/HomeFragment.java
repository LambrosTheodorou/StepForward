package com.example.uploadimage.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.uploadimage.Adapter.PostAdapter;
import com.example.uploadimage.Adapter.UserAdapter;
import com.example.uploadimage.MessageActivity;
import com.example.uploadimage.MessageUserActivity;
import com.example.uploadimage.Model.Post;
import com.example.uploadimage.Model.User;
import com.example.uploadimage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;

    private List<String> followingList;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        TextView message_system = view.findViewById(R.id.message_system);

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postList);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(postAdapter);

        progressBar = view.findViewById(R.id.progress_bar);

        readPosts();

        message_system.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MessageActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


    private void readPosts(){
        final DatabaseReference currentUserDB = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        final DatabaseReference postDB = FirebaseDatabase.getInstance().getReference("Posts");
        final DatabaseReference usersDB = FirebaseDatabase.getInstance().getReference("Users");

        final String[] currentUsersNation = new String[1];
        currentUserDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userObj = snapshot.getValue(User.class);
                currentUsersNation[0] = userObj.getNation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final List usersList = new ArrayList<String>();
        usersDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User userObj = snapshot.getValue(User.class);
                    String usersNation = userObj.getNation();
                    if(!currentUsersNation[0].equals(usersNation)){
                        usersList.add(snapshot.child("id").getValue().toString());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        postDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                postList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        for (int i = 0; i < usersList.size(); i++) {
                            Post post = snapshot.getValue(Post.class);
                            if(usersList.get(i).equals(post.getPublisher())){
                                postList.add(post);
                            }
                        }
                }
                    postAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}