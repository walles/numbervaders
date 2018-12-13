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
import android.widget.TextView;

import java.util.List;

// FIXME: All the drawables should actually be drawables and be retrieved using Medal.getDrawable()
public class MedalsAdapter extends RecyclerView.Adapter<MedalsAdapter.MedalViewHolder> {
    private final List<Medal> medals;

    public MedalsAdapter(List<Medal> medals) {
        this.medals = medals;
    }

    static class MedalViewHolder extends RecyclerView.ViewHolder {
        private final TextView drawable;
        private final TextView description;

        public MedalViewHolder(View itemView, TextView drawable, TextView description) {
            super(itemView);
            this.drawable = drawable;
            this.description = description;
        }
    }

    @NonNull
    @Override
    public MedalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View medalView = LayoutInflater.from(parent.getContext()).inflate(R.layout.medal_view, parent, false);
        TextView drawable = medalView.findViewById(R.id.drawable);
        TextView description = medalView.findViewById(R.id.description);

        return new MedalViewHolder(medalView, drawable, description);
    }

    @Override
    public void onBindViewHolder(@NonNull MedalViewHolder holder, int position) {
        Medal medal = medals.get(position);
        holder.description.setText(medal.getDescription());
        holder.drawable.setText("[XX]");
    }

    @Override
    public int getItemCount() {
        return medals.size();
    }
}
