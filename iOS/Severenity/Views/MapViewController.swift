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
        Log.info(message: "Map VIPER module init did complete", sender: self)
    }
    
    // MARK: Loading view
    
    override func viewDidLoad() {
        super.viewDidLoad()
        Log.info(message: "Map tab did load", sender: self)
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
            if let currentLatitude = locationManager.location?.coordinate.latitude, let currentLongitude = locationManager.location?.coordinate.longitude {
                mapView.moveCamera(GMSCameraUpdate.setCamera(GMSCameraPosition.camera(withLatitude: currentLatitude,
                                                                                      longitude: currentLongitude,
                                                                                      zoom: 19)))
                presenter?.readyToLoadPlacesNearLocation(lng: currentLongitude, lat: currentLatitude)
            }
        }
    }
    
    /**- Calling this method simply adjust GoogleMap to see all markers */
    internal func showAllMarkersOnMap() {
        guard let firstPlace = markers.first?.value.position else {
            Log.error(message: "Cannot zoom map to see all markers", sender: self)
            return
        }
        var bounds = GMSCoordinateBounds.init(coordinate: firstPlace, coordinate: firstPlace)
        for marker in markers {
            bounds = bounds.includingCoordinate(marker.value.position)
        }
        mapView.animate(with: GMSCameraUpdate.fit(bounds, with: UIEdgeInsetsMake(50, 50, 50, 50)))
    }
    
    internal func iconForPlace(type: PlaceType) -> UIImage? {
        var icon: UIImage?
        switch type {
        case .def:
            icon = #imageLiteral(resourceName: "place_experience_violet.png")
        case .money:
            icon = #imageLiteral(resourceName: "place_money_violet.png")
        case .implantRecovery:
            icon = #imageLiteral(resourceName: "place_implant_recovery_violet.png")
        case .implantRepair:
            icon = #imageLiteral(resourceName: "place_implant_repair_violet.png")
        case .implantIncrease:
            icon = #imageLiteral(resourceName: "place_implant_increase_violet.png")
        case .energyIncrease:
            icon = #imageLiteral(resourceName: "place_energy_increase_violet.png")
        case .immunityIncrease:
            icon = #imageLiteral(resourceName: "place_immunity_increase_violet.png")
        }
        return icon
    }

}

// MARK: MapPresenter delegate

extension MapViewController: MapPresenterDelegate {
    
    func addPlaceToMapWith(dictionary: Dictionary<String,Any>) {
        Log.info(message: "MapPresenter did call MapViewController with data: \(dictionary)", sender: self)
        let marker = GMSMarker()
        if let lat = dictionary["lat"] as? Double, let lng = dictionary["lng"] as? Double {
            marker.position = CLLocationCoordinate2DMake(lat, lng)
        }
        marker.title = dictionary["name"] as? String
        if let placeType = dictionary["type"] as? Int {
            marker.icon = iconForPlace(type: PlaceType(rawValue: placeType)!)
        }
        marker.map = mapView
        if let placeId = dictionary["placeId"] as? String {
            markers[placeId] = marker
        }
        Log.info(message: "New place marker added to the map", sender: self)
        tabBarController?.selectedIndex = 2
        //showAllMarkersOnMap()
    }
    
    func addPlayerToMapWith(image: UIImage, coordinates: CLLocationCoordinate2D, info: Dictionary<String,String>) {
        guard let userID = info["id"], let userName = info["name"] else {
            Log.error(message: "Cannot add player marker to map with recieved info", sender: self)
            return
        }
        var customImage = image.roundedImageWithBorder(with: 5, and: #colorLiteral(red: 0.5176470588, green: 0.3411764706, blue: 0.6, alpha: 1))
        customImage = customImage?.imageResize(sizeChange: CGSize(width: 45, height: 45))
        if markers[userID] != nil {
            markers[userID]?.icon = customImage
            markers[userID]?.position = coordinates
            markers[userID]?.title = userName
            Log.info(message: "Recieved player marker is already on the map. Coordinates were updated.", sender: self)
        } else {
            let marker = GMSMarker()
            marker.position = coordinates
            marker.title = userName
            marker.icon = customImage
            marker.map = mapView
            markers[userID] = marker
            Log.info(message: "New player marker added to the map", sender: self)
        }
    }
}

// MARK: GMSMapView delegate

extension MapViewController: GMSMapViewDelegate {
    
    func mapView(_ mapView: GMSMapView, didTap marker: GMSMarker) -> Bool {
        mapView.moveCamera(GMSCameraUpdate.setCamera(GMSCameraPosition.camera(withLatitude: marker.position.latitude,
                                                                              longitude: marker.position.longitude,
                                                                              zoom: 19)))
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
