package com.example.aaup8v2.aaup8v2.fragments.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aaup8v2.aaup8v2.MainActivity;
import com.example.aaup8v2.aaup8v2.QueueElement;
import com.example.aaup8v2.aaup8v2.R;

import java.util.List;

/**
 * Created by Sean Skov Them on 02-05-2016.
 */
public class QueueListAdapter extends ArrayAdapter<QueueElement>{

    public QueueListAdapter(Context context, int resource, List<QueueElement> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.queue_listview_element, null);
        }

        QueueElement element = getItem(position);

        if (element != null) {
            // Creates all the views
            TextView  trackName      = (TextView)  v.findViewById(R.id.track_name);
            TextView  trackArtist    = (TextView)  v.findViewById(R.id.artist_name);
            TextView  upVotes        = (TextView)  v.findViewById(R.id.upCount);
            TextView  downVotes      = (TextView)  v.findViewById(R.id.downCount);
            ImageView upVoteButton   = (ImageView) v.findViewById(R.id.upVote);
            ImageView downVoteButton = (ImageView) v.findViewById(R.id.downVote);

            if (trackName != null)
                trackName.setText(element.track.name);

            if (trackArtist != null) {
                String artistText = "Artist: " + element.track.artist;
                trackArtist.setText(artistText);
            }

            if (downVotes != null) {
                String downVoteNumber = Integer.toString(element.downvoteList.size());
                downVotes.setText(downVoteNumber);
            }

            if (upVotes != null) {
                String upVoteNumber = Integer.toString(element.upvoteList.size());
                upVotes.setText(upVoteNumber);
            }

            if (upVoteButton != null) {
                upVoteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity.mQueueFragment.click_up_vote(position);
                    }
                });

                int like = R.drawable.ic_action_like;
                int likeActive = R.drawable.ic_action_like_active;
                if(element.upvoteList.contains(MainActivity.mQueueFragment.myIP))
                    upVoteButton.setImageResource(likeActive);
                else
                    upVoteButton.setImageResource(like);
            }

            if (downVoteButton != null) {
                downVoteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       MainActivity.mQueueFragment.click_down_vote(position);
                    }
                });
                int dontlikeActive = R.drawable.ic_action_dontlike_active;
                int dontlike = R.drawable.ic_action_dontlike;
                if(element.downvoteList.contains(MainActivity.mQueueFragment.myIP))
                    downVoteButton.setImageResource(dontlikeActive);
                else
                    downVoteButton.setImageResource(dontlike);
            }
        }
        return v;
    }
}
