//
//  Tab3ViewController.swift
//  severenityProject
//
//  Created by Yura Yasinskyy on 12.09.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit
import GoogleMaps

class MapViewController: UIViewController {

    internal var presenter: MapPresenter?
    internal var markers: [String: GMSMarker] = [:]
    let locationManager = CLLocationManager()
    
    @IBOutlet var mapView: GMSMapView!
    
    // MARK: Init
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        presenter = MapPresenter()
        presenter?.delegate = self
        print("Map VIPER module init did complete")
    }
    
    // MARK: Loading view
    
    override func viewDidLoad() {
        super.viewDidLoad()
        print("Map tab did load")
        
        locationManager.requestWhenInUseAuthorization()
        if CLLocationManager.locationServicesEnabled() {
            locationManager.delegate = self
            locationManager.desiredAccuracy = kCLLocationAccuracyBest
            locationManager.startUpdatingLocation()
        }
        if CLLocationManager.authorizationStatus() == .denied {
            let locationAlertController = UIAlertController(title: "Hello dear user!", message: kNeedsLocationServicesAccess, preferredStyle: .alert)
            let defaultAction = UIAlertAction.init(title: "Ok", style: .default, handler: { (action) in
                exit(0)
            })
            locationAlertController.addAction(defaultAction)
            present(locationAlertController, animated: true, completion: nil)
        } else {
            mapView.delegate = self
            view = mapView
            mapView.isMyLocationEnabled = false
        }
    }
    
    /**- Calling this method simply adjust Google Map to see all markers */
    internal func showAllMarkersOnMap() {
        guard let firstPlace = markers.first?.value.position else {
            print("Cannot zoom map to see all markers")
            return
        }
        var bounds = GMSCoordinateBounds.init(coordinate: firstPlace, coordinate: firstPlace)
        for marker in markers {
            bounds = bounds.includingCoordinate(marker.value.position)
        }
        mapView.animate(with: GMSCameraUpdate.fit(bounds, with: UIEdgeInsetsMake(50, 50, 50, 50)))
    }

}

// MARK: MapPresenter delegate

extension MapViewController: MapPresenterDelegate {
    
    func addNewPlaceToMap(with data: Dictionary<String,Any>) {
        print("MapPresenter did call MapViewController with data: \(data)")
        let marker = GMSMarker()
        if let lat = data["lat"] as? Double, let lng = data["lng"] as? Double {
            marker.position = CLLocationCoordinate2DMake(lat, lng)
        }
        marker.title = data["name"] as? String
        marker.map = mapView
        if let placeId = data["placeId"] as? String {
            markers[placeId] = marker
        }
        print("New place marker added to the map")
        showAllMarkersOnMap()
    }
    
    func addNewPlayerToMap(with image: UIImage, and coordinates: CLLocationCoordinate2D, and info: Dictionary<String,String>) {
        guard let userID = info["id"], let userName = info["name"] else {
            print("Cannot add player marker to map with recieved info")
            return
        }
        var customImage = image.roundedImageWithBorder(with: 5, and: #colorLiteral(red: 0.5176470588, green: 0.3411764706, blue: 0.6, alpha: 1))
        customImage = customImage?.imageResize(sizeChange: CGSize.init(width: 45, height: 45))
        if markers[userID] != nil {
            markers[userID]?.icon = customImage
            markers[userID]?.position = coordinates
            markers[userID]?.title = userName
            print("Recieved player marker is already on the map. Coordinates were updated.")
        } else {
            let marker = GMSMarker()
            marker.position = coordinates
            marker.title = userName
            marker.icon = customImage
            marker.map = mapView
            markers[userID] = marker
            print("New player marker added to the map")
        }
    }
    
}

// MARK: GMSMapViewDelegate

extension MapViewController: GMSMapViewDelegate {
    
    func mapView(_ mapView: GMSMapView, didTap marker: GMSMarker) -> Bool {
        mapView.moveCamera(GMSCameraUpdate.setCamera(GMSCameraPosition.camera(withLatitude: marker.position.latitude,
                                                                              longitude: marker.position.longitude,
                                                                              zoom: 18)))
        mapView.selectedMarker = marker
        return true
    }
    
}

// MARK: CLLocationManager delegate

extension MapViewController: CLLocationManagerDelegate {
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        if let currentLocation = locations.first {
            presenter?.userLocationUpdate(currentLocation)
        }
    }
    
}
