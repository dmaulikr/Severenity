//
//  Tab3ViewController.swift
//  severenityProject
//
//  Created by Yura Yasinskyy on 12.09.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit
import GoogleMaps

class MapViewController: UIViewController, CLLocationManagerDelegate {

    let locationManager = CLLocationManager()

    var recievedLocation: [String: AnyObject] = [:]
    fileprivate var recievedLocationCoordinates: CLLocationCoordinate2D = CLLocationCoordinate2DMake(0, 0)
    
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
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.locationManager.requestWhenInUseAuthorization()
        if CLLocationManager.locationServicesEnabled() {
            self.locationManager.delegate = self
            self.locationManager.desiredAccuracy = kCLLocationAccuracyBest
            self.locationManager.startUpdatingLocation()
        }
    }
}
