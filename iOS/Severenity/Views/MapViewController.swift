//
//  Tab3ViewController.swift
//  severenityProject
//
//  Created by Yura Yasinskyy on 12.09.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit
import GoogleMaps

class MapViewController: UIViewController, CLLocationManagerDelegate, MapPresenterDelegate, GMSMapViewDelegate {

    var recievedLocation: [String: AnyObject] = [:]
    private var recievedLocationCoordinates = CLLocationCoordinate2D()
    private var presenter: MapPresenter?
    let locationManager = CLLocationManager()
    
    @IBOutlet var mapView: GMSMapView!
    
    // MARK: - Init
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        presenter = MapPresenter()
        presenter?.delegate = self
        print("Map VIPER module init did complete")
    }
    
    // MARK: - Loading view
    
    override func viewWillAppear(_ animated: Bool) {
        if CLLocationManager.authorizationStatus() == .authorizedWhenInUse, let coordinates = locationManager.location?.coordinate {
            let camera = GMSCameraPosition.camera(withLatitude: coordinates.latitude,
                                                              longitude: coordinates.longitude, zoom: 15)
            mapView = GMSMapView.map(withFrame: CGRect.zero, camera: camera)
            mapView.isMyLocationEnabled = true
            view = mapView
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        print("Map tab did load")
        
        self.locationManager.requestWhenInUseAuthorization()
        if CLLocationManager.locationServicesEnabled() {
            self.locationManager.delegate = self
            self.locationManager.desiredAccuracy = kCLLocationAccuracyBest
            self.locationManager.startUpdatingLocation()
        }
    }
    
    // MARK: - MapPresenter delegate
    
    func mapPresenterDidCallView(with data: Dictionary<String,AnyObject>) {
        print("MapPresenter did call MapViewController with data: \(data)")
        recievedLocation = data
    }
    
    func addNewPinToMap(with image: UIImage, and coordinates: CLLocationCoordinate2D) {
        let marker = GMSMarker()
        marker.position = coordinates
        marker.title = ""
        marker.snippet = ""
        marker.icon = image
        marker.map = mapView
    }
    
    // MARK: - CLLocationManager delegate
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        if let currentLocation = locations.first {
            presenter?.userLocationChange(currentLocation)
        }
    }
}
