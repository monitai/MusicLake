package com.cyl.musiclake.ui.localmusic.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.cyl.musiclake.R;
import com.cyl.musiclake.data.model.Music;
import com.cyl.musiclake.service.PlayManager;
import com.cyl.musiclake.ui.base.BaseFragment;
import com.cyl.musiclake.ui.localmusic.adapter.SongAdapter;
import com.cyl.musiclake.ui.localmusic.contract.AlbumDetailContract;
import com.cyl.musiclake.ui.localmusic.dialog.AddPlaylistDialog;
import com.cyl.musiclake.ui.localmusic.dialog.ShowDetailDialog;
import com.cyl.musiclake.ui.localmusic.presenter.AlbumDetailPresenter;
import com.cyl.musiclake.ui.common.Extras;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 作者：yonglong on 2016/8/15 19:54
 * 邮箱：643872807@qq.com
 * 版本：2.5
 * 专辑
 */
public class AlbumDetailFragment extends BaseFragment implements AlbumDetailContract.View {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsing_toolbar;
    @BindView(R.id.album_art)
    ImageView album_art;


    long albumID;
    String transitionName;
    String title;

    private SongAdapter mAdapter;
    private List<Music> musicInfos = new ArrayList<>();
    private AlbumDetailPresenter mPresenter;

    public static AlbumDetailFragment newInstance(long id, String title, String transitionName) {
        Bundle args = new Bundle();
        args.putLong(Extras.ALBUM_ID, id);
        args.putString(Extras.PLAYLIST_NAME, title);
        args.putString(Extras.TRANSITIONNAME, transitionName);
        AlbumDetailFragment fragment = new AlbumDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void initDatas() {
        albumID = getArguments().getLong(Extras.ALBUM_ID);
        transitionName = getArguments().getString(Extras.TRANSITIONNAME);
        title = getArguments().getString(Extras.PLAYLIST_NAME);

        if (transitionName != null)
            album_art.setTransitionName(transitionName);
        if (title != null)
            collapsing_toolbar.setTitle(title);
        mPresenter.loadAlbumSongs(albumID);
        mPresenter.loadAlbumArt(albumID);
    }

    @Override
    public int getLayoutId() {
        return R.layout.frag_album;
    }

    @Override
    public void initViews() {
        mPresenter = new AlbumDetailPresenter(getContext());
        mPresenter.attachView(this);

        if (((AppCompatActivity) getActivity()) != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
            ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
            ab.setDisplayHomeAsUpEnabled(true);
        }

        mAdapter = new SongAdapter(musicInfos);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.bindToRecyclerView(mRecyclerView);
    }

    @Override
    protected void listener() {
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (view.getId() != R.id.iv_more) {
                List<Music> musicList = adapter.getData();
                PlayManager.setPlayList(musicList);
                PlayManager.play(position);
            }
        });
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            PopupMenu popupMenu = new PopupMenu(getContext(), view);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.popup_song_play:
                        PlayManager.setPlayList((List<Music>) adapter.getData());
                        PlayManager.play(position);
                        break;
                    case R.id.popup_song_detail:
                        ShowDetailDialog.newInstance((Music) adapter.getItem(position))
                                .show(getChildFragmentManager(), getTag());
                        break;
                    case R.id.popup_song_addto_queue:
                        AddPlaylistDialog.newInstance(musicInfos.get(position))
                                .show(getChildFragmentManager(), "ADD_PLAYLIST");
                        break;

                }
                return false;
            });
            popupMenu.inflate(R.menu.popup_album);
            popupMenu.show();
        });
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showEmptyView() {
        mAdapter.setEmptyView(R.layout.view_song_empty);
    }

    @Override
    public void showAlbumSongs(List<Music> songList) {
        mAdapter.setNewData(songList);
    }


    @Override
    public void showAlbumArt(Drawable albumArt) {

    }

    @Override
    public void showAlbumArt(Bitmap bitmap) {
        album_art.setImageBitmap(bitmap);
    }

}