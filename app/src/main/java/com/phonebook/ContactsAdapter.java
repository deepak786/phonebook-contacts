package com.phonebook;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.phonebook.databinding.ListItemContactBinding;

import java.util.List;

/**
 * adapter class to display the data on Recycler View
 */
public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.Holder> {
    private List<Contact> contacts;

    public ContactsAdapter(List<Contact> contacts) {
        this.contacts = contacts;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        ListItemContactBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.list_item_contact, viewGroup, false);

        return new Holder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        holder.binding.setObj(contacts.get(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        if (contacts != null && contacts.size() > 0)
            return contacts.size();
        return 0;
    }

    class Holder extends RecyclerView.ViewHolder {
        private ListItemContactBinding binding;

        Holder(@NonNull ListItemContactBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
