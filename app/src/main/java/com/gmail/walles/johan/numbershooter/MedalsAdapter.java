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

import android.content.Context;
import android.graphics.drawable.Drawable;
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
    private final Context context;

    public MedalsAdapter(Context context, int medalSizePixels, List<Medal> medals) {
        this.medalSizePixels = medalSizePixels;
        this.medals = medals;
        this.context = context;
    }

    static class MedalViewHolder extends RecyclerView.ViewHolder {
        private final Drawable medalDrawable;

        public MedalViewHolder(Drawable medalDrawable, View itemView) {
            super(itemView);

            this.medalDrawable = medalDrawable;
        }

        public void bind(Medal medal, int medalSizePixels) {
            ImageView imageView = itemView.findViewById(R.id.medalImage);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(medalSizePixels, medalSizePixels));
            imageView.setImageDrawable(medalDrawable);
            imageView.setColorFilter(medal.flavor.color);

            TextView description = itemView.findViewById(R.id.description);
            description.setText(medal.getDescription());
        }
    }

    @NonNull
    @Override
    public MedalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View medalView = LayoutInflater.from(parent.getContext()).inflate(R.layout.medal_view, parent, false);

        Drawable medalDrawable = context.getDrawable(R.drawable.medal);
        if (medalDrawable == null) {
            throw new RuntimeException("Failed to load medal image");
        }

        return new MedalViewHolder(medalDrawable, medalView);
    }

    @Override
    public void onBindViewHolder(@NonNull MedalViewHolder holder, int position) {
        holder.bind(medals.get(position), medalSizePixels);
    }

    @Override
    public int getItemCount() {
        return medals.size();
    }
}
