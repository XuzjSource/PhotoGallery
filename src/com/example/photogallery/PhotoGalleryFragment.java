package com.example.photogallery;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class PhotoGalleryFragment extends Fragment {
	GridView mGridView;
	
	ArrayList<GalleryItem> mItems;
	
	ThumbnailDownloader<ImageView> mThumbnailThread;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		new FetchItemsTask().execute();
		
//		mThumbnailThread = new ThumbnailDownloader<ImageView>();
		mThumbnailThread = new ThumbnailDownloader<ImageView>(new Handler());
		mThumbnailThread.setListener(new ThumbnailDownloader.Listener<ImageView>() {
			@Override
			public void onThumbnailDownloaded(ImageView imageView, Bitmap thumbnail) {
				// TODO Auto-generated method stub
				if(isVisible()){
					imageView.setImageBitmap(thumbnail);
					
				}
			}
		});
		
		mThumbnailThread.start();
		mThumbnailThread.getLooper();
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_photo_gallery, container,false);
		mGridView = (GridView) v.findViewById(R.id.gridView);
		
		setupAdapter();
		return v;
	}

	private void setupAdapter() {
		// TODO Auto-generated method stub
		if(getActivity()==null || mGridView==null) return;
		if(mItems!=null){
			/*mGridView.setAdapter(new ArrayAdapter<GalleryItem>(getActivity(),
					android.R.layout.simple_gallery_item,mItems));*/
			mGridView.setAdapter(new GalleryItemAdapter(mItems));
		}else{
			mGridView.setAdapter(null);
		}
	}
	
	private class GalleryItemAdapter extends ArrayAdapter<GalleryItem>{

		public GalleryItemAdapter(ArrayList<GalleryItem> items) {
			super(getActivity(), 0,items);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if(convertView ==null) {
				convertView = getActivity().getLayoutInflater()
						.inflate(R.layout.gallery_item,parent,false);
			}
			ImageView imageView = (ImageView) convertView.findViewById(R.id.gallery_item_imageView);
			imageView.setImageResource(R.drawable.brian_up_close);
			GalleryItem item = getItem(position);
			mThumbnailThread.queueThumbnail(imageView, item.getUrl());
			return convertView;
		}
		
	}

	private class FetchItemsTask extends AsyncTask<Void, Void, ArrayList<GalleryItem>>{
		@Override
		protected ArrayList<GalleryItem> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			/*try {
				String result = new FlickrFetchr().getUrl("http://www.google.com");
			} catch (IOException c) {
				// TODO Auto-generated catch block
				c.printStackTrace();
			}*/
			return new FlickrFetchr().fetchItems();
		}

		//onPostExecute(...)方法在doInBackground(...)方法执行完毕后才会运行，而且它是在
		//主线程而非后台线程上运行的
		@Override
		protected void onPostExecute(ArrayList<GalleryItem> result) {
			// TODO Auto-generated method stub
			mItems = result;
			setupAdapter();
		}
		
		
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mThumbnailThread.quit();
	}
	
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		mThumbnailThread.clearQueue();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
