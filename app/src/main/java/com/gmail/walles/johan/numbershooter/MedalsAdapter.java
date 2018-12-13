/*
 * Copyright 2018, Johan Walles <johan.walles@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gmail.walles.johan.numbershooter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class MedalsAdapter extends RecyclerView.Adapter<MedalsAdapter.MedalViewHolder> {
    private final int medalSizePixels;
    private final List<Medal> medals;

    public MedalsAdapter(int medalSizePixels, List<Medal> medals) {
        this.medalSizePixels = medalSizePixels;
        this.medals = medals;
    }

    static class MedalViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView description;

        public MedalViewHolder(View itemView, ImageView imageView, TextView description) {
            super(itemView);
            this.imageView = imageView;
            this.description = description;
        }
    }

    @NonNull
    @Override
    public MedalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View medalView = LayoutInflater.from(parent.getContext()).inflate(R.layout.medal_view, parent, false);

        ImageView imageView = medalView.findViewById(R.id.medalImage);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(medalSizePixels, medalSizePixels));

        TextView description = medalView.findViewById(R.id.description);

        return new MedalViewHolder(medalView, imageView, description);
    }

    @Override
    public void onBindViewHolder(@NonNull MedalViewHolder holder, int position) {
        Medal medal = medals.get(position);
        holder.description.setText(medal.getDescription());
        holder.imageView.setImageDrawable(medal.getDrawable());
    }

    @Override
    public int getItemCount() {
        return medals.size();
    }
}
