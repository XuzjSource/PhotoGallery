package com.example.photogallery;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.net.Uri;

public class FlickrFetchr {

	public static final String TAG = "FlickrFetchr";
	private static final String ENDPOINT = "https://api.flickr.com/services/rest/";
	private static final String API_KEY = "67a22d38b6a113ba800def831035e5a6";
	private static final String METHOD_GET_RECENT = "flickr.photos.getRecent";
	private static final String PARAM_EXTRAS = "extras";
	private static final String EXTRA_SMALL_URL = "url_s"; // 399

	private static final String XML_PHOTO = "photo";

	public byte[] getUrlBytes(String urlSpec) throws IOException {
		URL url = new URL(urlSpec);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			InputStream in = connection.getInputStream();
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				return null;
			}
			int bytesRead = 0;
			byte[] buffer = new byte[1024];
			while ((bytesRead = in.read(buffer)) > 0) {
				out.write(buffer, 0, bytesRead);
			}
			out.close();
			return out.toByteArray();
		} finally {
			connection.disconnect();
		}
	}

	public String getUrl(String urlSpec) throws IOException {
		return new String(getUrlBytes(urlSpec));
	}

	public ArrayList<GalleryItem> fetchItems() {
		ArrayList<GalleryItem> items = new ArrayList<GalleryItem>();
		try {
			String url = Uri.parse(ENDPOINT).buildUpon()
					.appendQueryParameter("method", METHOD_GET_RECENT)
					.appendQueryParameter("api_key", API_KEY)
					.appendQueryParameter(PARAM_EXTRAS, EXTRA_SMALL_URL)
					.build().toString();
			System.out.println(url + "    url");
//			String xmlString = getUrl(url);
			String xmlString = "<rsp stat='ok'>"
							+"<photos page='1' pages='10' perpage='100' total='1000'>"
							+"<photo id='23904566974' owner='138303867@N02' secret='a0d5e513db' server='1614' farm='2' title='111' ispublic='1' isfriend='0' isfamily='0' url_s='https://farm2.staticflickr.com/1614/23904566974_a0d5e513db_m.jpg' height_s='240' width_s='195'/>"
							+"<photo id='23904583154' owner='25012306@N07' secret='fcd24e38d6' server='1662' farm='2' title='222' ispublic='1' isfriend='0' isfamily='0' url_s='https://farm2.staticflickr.com/1662/23904583154_fcd24e38d6_m.jpg' height_s='180' width_s='240'/>"
							+"<photo id='24532764545' owner='90252819@N03' secret='02965cd7dc' server='1709' farm='2' title='THE BLACK CROW' ispublic='1' isfriend='0' isfamily='0' url_s='https://farm2.staticflickr.com/1709/24532764545_02965cd7dc_m.jpg' height_s='240' width_s='160'/>"
							+"<photo id='24532769235' owner='60594606@N00' secret='b02a095d6e' server='1559' farm='2' title='3333' ispublic='1' isfriend='0' isfamily='0' url_s='https://farm2.staticflickr.com/1559/24532769235_b02a095d6e_m.jpg' height_s='160' width_s='240'/>"
							+"<photo id='24532769345' owner='132191813@N08' secret='9b4b1f474c' server='1505' farm='2' title='444' ispublic='1' isfriend='0' isfamily='0' url_s='https://farm2.staticflickr.com/1505/24532769345_9b4b1f474c_m.jpg' height_s='240' width_s='194'/>"
							+"</photos>"
							+"</rsp>";
			System.out.println(xmlString + "    xmlString");
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(new StringReader(xmlString));
			parseItems(items, parser);
		} catch (Exception c) {
			// TODO: handle exception
		}
		return items;
	}

	void parseItems(ArrayList<GalleryItem> items, XmlPullParser parser)
			throws XmlPullParserException, IOException {
		int eventType = parser.next();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_TAG
					&& XML_PHOTO.equals(parser.getName())) {
				String id = parser.getAttributeValue(null, "id");
				String caption = parser.getAttributeValue(null, "title");
				String smallUrl = parser.getAttributeValue(null,EXTRA_SMALL_URL);
				GalleryItem item = new GalleryItem();
				item.setId(id);
				item.setCaption(caption);
				item.setUrl(smallUrl);
				items.add(item);
			}
			eventType = parser.next();
		}
	}

}
