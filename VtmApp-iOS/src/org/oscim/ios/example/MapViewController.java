package org.oscim.ios.example;

import java.util.ArrayList;
import java.util.List;

import org.oscim.backend.canvas.Bitmap;
import org.oscim.core.GeoPoint;
import org.oscim.core.MapPosition;
import org.oscim.ios.IosMapView;
import org.oscim.ios.Utils;
import org.oscim.ios.canvas.IosGraphics;
import org.oscim.layers.marker.ItemizedLayer;
import org.oscim.layers.marker.ItemizedLayer.OnItemGestureListener;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerItem.HotspotPlace;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.tiling.source.mapfile.MapFileTileSource;
import org.robovm.apple.foundation.Foundation;
import org.robovm.apple.foundation.NSBundle;
import org.robovm.apple.uikit.UIInterfaceOrientation;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UIViewController;

public class MapViewController extends UIViewController implements OnItemGestureListener<MarkerItem>{

	IosMapView mapView;
	MarkerSymbol mFocusMarker;
	
	@Override
    public void viewDidLoad () {
        super.viewDidLoad();
        
        mapView = new IosMapView(UIScreen.getMainScreen().getBounds()){
        	@Override
			public void createLayers() {
				try {
					MapFileTileSource ts = new MapFileTileSource();
					String file = NSBundle.getMainBundle().getBundlePath() + "/cienfuegos.map";
										
					if (ts.setMapFile(file)) {
						initDefaultLayers(ts, false, true, false);
						
						MapPosition pos = new MapPosition(22.150524, -80.450134, 1 << 11);
						getMap().setMapPosition(pos);
						
						Bitmap bitmap = IosGraphics.getBitmapAsset("marker_poi.png");
						MarkerSymbol symbol = new MarkerSymbol(bitmap, HotspotPlace.CENTER);
						
						Bitmap focus = IosGraphics.getBitmapAsset("Icon.png");
						mFocusMarker = new MarkerSymbol(focus, HotspotPlace.CENTER);
						
						ItemizedLayer<MarkerItem> markerLayer =  new ItemizedLayer<MarkerItem>(mMap, new ArrayList<MarkerItem>(), symbol, (OnItemGestureListener<MarkerItem>) MapViewController.this);
					
						mMap.layers().add(markerLayer);
						
						List<MarkerItem> pts = new ArrayList<MarkerItem>();

						for (double lat = 22.10; lat <= 22.20; lat += 0.1) {
							for (double lon = -81; lon <= -80; lon += 0.1)
								pts.add(new MarkerItem(lat + "/" + lon, "",
								                       new GeoPoint(lat, lon)));
						}

						markerLayer.addItems(pts);
					}
				} catch (Throwable e) {
					Utils.printThrowable(e);
				}
			}
        };
        getView().addSubview(mapView);
        //getView().addSubview(new TempView(getView().getBounds()));
        
//        for (NSString fontName: UIFont.getFontNamesForFamilyName("Arial")){
//        	Foundation.log(fontName.toString());
//        }
	}
	
	@Override
	public void didRotate (UIInterfaceOrientation orientation) {
		super.didRotate(orientation);
		
		mapView.onRotate();
	}

	@Override
	public boolean onItemSingleTapUp(int index, MarkerItem item) {
		Foundation.log("onItemSingleTapUp, item = " + item.title);
		
		if (item.getMarker() == null){
			item.setMarker(mFocusMarker);
			mapView.getMap().animator().animateTo(item.getPoint());
		}
		else
			item.setMarker(null);
		return false;
	}

	@Override
	public boolean onItemLongPress(int index, MarkerItem item) {
		// TODO Auto-generated method stub
		return false;
	}
}
