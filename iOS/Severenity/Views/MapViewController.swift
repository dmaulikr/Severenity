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
    private var markers: [String: GMSMarker] = [:]
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
    
    override func viewDidLoad() {
        super.viewDidLoad()
        print("Map tab did load")
        
        locationManager.requestWhenInUseAuthorization()
        if CLLocationManager.locationServicesEnabled() {
            locationManager.delegate = self
            locationManager.desiredAccuracy = kCLLocationAccuracyBest
            locationManager.startUpdatingLocation()
        }
        
        view = mapView
        mapView.isMyLocationEnabled = false
    }
    
    func updateMarker(with image: UIImage, and coordinates: CLLocationCoordinate2D, and fbUserId: String) {
        
    }
    
    
    
    // MARK: - MapPresenter delegate
    
    func mapPresenterDidCallView(with data: Dictionary<String,AnyObject>) {
        print("MapPresenter did call MapViewController with data: \(data)")
        recievedLocation = data
    }
    
    func addNewMarkerToMap(with image: UIImage, and coordinates: CLLocationCoordinate2D, and fbUserId: String) {
        if markers[fbUserId] != nil {
            print("Recieved marker is already on the map. Please update it.")
        }
        else {
            let marker = GMSMarker()
            marker.position = coordinates
            marker.title = ""
            marker.snippet = ""
            var customImage = image.roundedImageWithBorder(with: 4, and: UIColor.black)
            customImage = customImage?.imageResize(sizeChange: CGSize.init(width: 45, height: 45))
            marker.icon = customImage
            marker.map = mapView
            markers[fbUserId] = marker
        }
    }
    
    // MARK: - CLLocationManager delegate
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        if CLLocationManager.authorizationStatus() == .authorizedWhenInUse ||
            CLLocationManager.authorizationStatus() == .authorizedAlways {
            if let currentLocation = locations.first {
                presenter?.userLocationChange(currentLocation)
            }
        }
    }
}

extension UIImage {
    
    func roundedImageWithBorder(with width: CGFloat, and color: UIColor) -> UIImage? {
        let square = CGSize(width: min(size.width, size.height) + width * 2, height: min(size.width, size.height) + width * 2)
        let imageView = UIImageView(frame: CGRect(origin: CGPoint(x: 0, y: 0), size: square))
        imageView.contentMode = .center
        imageView.image = self
        imageView.layer.cornerRadius = square.width/2
        imageView.layer.masksToBounds = true
        imageView.layer.borderWidth = width
        imageView.layer.borderColor = color.cgColor
        UIGraphicsBeginImageContextWithOptions(imageView.bounds.size, false, scale)
        guard let context = UIGraphicsGetCurrentContext() else { return nil }
        imageView.layer.render(in: context)
        let result = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return result
    }
    
    func imageResize (sizeChange:CGSize)-> UIImage{
        let hasAlpha = true
        let scale: CGFloat = 0.0 // Use scale factor of main screen
        UIGraphicsBeginImageContextWithOptions(sizeChange, !hasAlpha, scale)
        self.draw(in: CGRect(origin: CGPoint.zero, size: sizeChange))
        let scaledImage = UIGraphicsGetImageFromCurrentImageContext()
        return scaledImage!
    }
}
