package com.example.uploadimage.Fragment;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Toolbar;

import com.example.uploadimage.Adapter.UserAdapter;
import com.example.uploadimage.MessageActivity;
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

public class MessageFragment extends Fragment {

    private UserAdapter userAdapter;
    private List<User> mUsers;
    RecyclerView recyclerView;
    private List<Post> postLists;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        //Toolbar toolbar = findViewById(R.id.toolbar);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        postLists = new ArrayList<>();
        mUsers = new ArrayList<>();
        readUsers();

        return view;
    }
    final List publisherGlobal = new ArrayList();

    private void readUsers() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Matches").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //mUsers.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    //User user = dataSnapshot.getValue(User.class);
                    String publisher = dataSnapshot.getValue().toString();
                    Log.d("Value", "Current User: " + publisher);

                    publisherGlobal.add(publisher);
//                    if (dataSnapshot.exists()) {
                        //publisher[0] = user.getId();
                        //mUsers.add(user);
//                        Log.d("Value", "User: " + publisherGlobal[0]);
                        getMatchedUsers();
//                    }
//                    Log.d("Value", "User: " + publisherGlobal[0]);
                }
//                userAdapter = new UserAdapter(getContext(), mUsers);
//                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getMatchedUsers(){
        DatabaseReference usersDB = FirebaseDatabase.getInstance().getReference("Users");

        usersDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                //Log.d("Value", "User: " + snapshot.getValue());
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    Log.d("Value", "User: " + user);

//                    Log.d("Value", "User: " + currentUsersNation[0]);
//                    assert user != null;
//                    assert firebaseUser != null;

                    for (int i=0; i < publisherGlobal.size(); i++){
                        if(publisherGlobal.get(i).equals(user.getId())){
                            mUsers.add(user);
                        }
                    }

                }
                userAdapter = new UserAdapter(getContext(), mUsers);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}