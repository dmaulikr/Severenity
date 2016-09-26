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
    
    var locationManager = CLLocationManager()

    var recievedLocation: [String: AnyObject] = [:]
    fileprivate var recievedLocationCoordinates: CLLocationCoordinate2D = CLLocationCoordinate2DMake(0, 0)
    
    // MARK: - Loading view
    
    override func viewWillAppear(_ animated: Bool) {
        
        if let recievedLocationLatitude = recievedLocation["locationLatitude"] as? Double,
            let recievedLocationLongtitude = recievedLocation["locationLongtitude"] as? Double {
                recievedLocationCoordinates = CLLocationCoordinate2DMake(recievedLocationLatitude, recievedLocationLongtitude)
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
        }
        marker.map = mapView
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        locationManager.delegate = self
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        locationManager.requestWhenInUseAuthorization()
        locationManager.startUpdatingLocation()
        
    }
    
}
