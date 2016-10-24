//
//  Tab3ViewController.swift
//  severenityProject
//
//  Created by Yura Yasinskyy on 12.09.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit
import GoogleMaps

class MapViewController: UIViewController, CLLocationManagerDelegate, MapPresenterDelegate {

    let locationManager = CLLocationManager()

    var recievedLocation: [String: AnyObject] = [:]
    fileprivate var recievedLocationCoordinates: CLLocationCoordinate2D = CLLocationCoordinate2DMake(0, 0)
    
    private var presenter: MapPresenter?
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        presenter = MapPresenter()
        presenter?.delegate = self
        print("Map VIPER module init did complete")
    }
    
    func mapPresenterDidCallView(with data: Dictionary<String,AnyObject>) {
        print("MapPresenter did call Map View with data: \(data)")
        recievedLocation = data
    }
    
    // MARK: - Loading view
    
    override func viewWillAppear(_ animated: Bool) {
        if CLLocationManager.authorizationStatus() == .authorizedWhenInUse {
            if let recievedLocationLatitude = recievedLocation["lat"] as? Double,
                let recievedLocationLongtitude = recievedLocation["lng"] as? Double {
                    recievedLocationCoordinates = CLLocationCoordinate2DMake(recievedLocationLatitude, recievedLocationLongtitude)
                    print("Displaying place with coordinates: \(recievedLocationCoordinates)")
            } else {
                if let defaultCoordinates = locationManager.location?.coordinate {
                    recievedLocationCoordinates = defaultCoordinates
                }
            }
            let camera = GMSCameraPosition.camera(withLatitude: recievedLocationCoordinates.latitude,
                                                              longitude: recievedLocationCoordinates.longitude, zoom: 15)
            let mapView = GMSMapView.map(withFrame: CGRect.zero, camera: camera)
            mapView.isMyLocationEnabled = true
            self.view = mapView
            let marker = GMSMarker()
            marker.position = recievedLocationCoordinates
            if let markerTitle = recievedLocation["name"] as? String {
                marker.title = markerTitle
            } else {
                marker.title = "Your current location"
            }
            marker.map = mapView
        } else {
            let camera = GMSCameraPosition.camera(withLatitude: -33.86,
                                                              longitude: 151.20, zoom: 6)
            let mapView = GMSMapView.map(withFrame: CGRect.zero, camera: camera)
            self.view = mapView
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
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        //presenter?.userLocationChange(locations.first!)
    }
}
