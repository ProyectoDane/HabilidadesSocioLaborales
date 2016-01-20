package com.belatrix.habilidadessociolaborales.ui.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.belatrix.habilidadessociolaborales.R;
import com.belatrix.habilidadessociolaborales.datamodel.User;
import com.belatrix.habilidadessociolaborales.datamodelmanagers.UserManager;
import com.belatrix.habilidadessociolaborales.ui.UserEditActivity;

import java.util.List;

public class ListViewUsersAdapter extends ArrayAdapter<User> {

    private Context mContext;
    private List<User> mUsersList = null;

    public ListViewUsersAdapter(Context context, List<User> usersList) {
        super(context, R.layout.item_list_login, usersList);
        this.mContext = context;
        this.mUsersList = usersList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View rowView = convertView;

        if (rowView == null) {

            rowView = LayoutInflater.from(mContext).inflate(R.layout.item_list_login, null);

            ItemUserViewHolder viewHolder = new ItemUserViewHolder();
            viewHolder.userNameTextView = (TextView) rowView.findViewById(R.id.userNameTextView);
            viewHolder.userLastNameTextView = (TextView) rowView.findViewById(R.id.userLastNameTextView);
            viewHolder.userImageView = (ImageView) rowView.findViewById(R.id.userImageView);
            viewHolder.userDeleteProfileButton = (ImageButton) rowView.findViewById(R.id.userDeleteProfileButton);
            viewHolder.userEditProfileButton = (ImageButton) rowView.findViewById(R.id.userEditProfileButton);

            rowView.setTag(viewHolder);
        }

        ItemUserViewHolder holder = (ItemUserViewHolder) rowView.getTag();

        final User currentUser = getItem(position);

        holder.userNameTextView.setText(currentUser.getName());
        holder.userLastNameTextView.setText(currentUser.getLastname());
        holder.userEditProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, UserEditActivity.class);
                intent.putExtra("SourceActivity", "ListViewUsersAdapter");
                intent.putExtra("userId", currentUser.getId());
                mContext.startActivity(intent);
            }
        });

        holder.userDeleteProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setTitle("Eliminar usuario");
                dialog.setMessage("Seguro desea eliminar el usuario " + mUsersList.get(position).getName() + " " + mUsersList.get(position).getLastname() + "? También se eliminarán todas las estadísticas asociadas al usuario");
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mUsersList.remove(position);
                        notifyDataSetChanged();
                        UserManager.getInstance(mContext).deleteUser(currentUser);
                    }
                });
                dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                dialog.create().show();
            }
        });

        if(mUsersList.get(position).getImage() != null) {
            holder.userImageView.setImageBitmap(BitmapFactory.decodeByteArray(mUsersList.get(position).getImage(), 0, mUsersList.get(position).getImage().length));
        } else {
            holder.userImageView.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.test_avatar));
        }

        return rowView;
    }

    public int getCount() {
        return mUsersList.size();
    }

    static class ItemUserViewHolder {
        public TextView userNameTextView;
        public TextView userLastNameTextView;
        public ImageView userImageView;
        public ImageButton userDeleteProfileButton;
        public ImageButton userEditProfileButton;
    }
}
