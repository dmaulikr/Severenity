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
        if CLLocationManager.authorizationStatus() == .denied {
            let locationAlertController = UIAlertController(title: "Hello user!", message: "Unfortunately Severenity needs access to location services to continue. Please turn it manually in Settings and come back. App will now exit", preferredStyle: .alert)
            let defaultAction = UIAlertAction.init(title: "Ok", style: .default, handler: { (action) in
                exit(0)
            })
            locationAlertController.addAction(defaultAction)
            present(locationAlertController, animated: true, completion: nil)
        }
        else {
            mapView.delegate = self
            view = mapView
            mapView.isMyLocationEnabled = false
        }
    }
    
    /**- Calling this method simply adjust Google Map to see all markers */
    private func showAllMarkersOnMap() {
        let firstPlace = markers.first?.value.position
        var bounds = GMSCoordinateBounds.init(coordinate: firstPlace!, coordinate: firstPlace!)
        for marker in markers {
            bounds = bounds.includingCoordinate(marker.value.position)
        }
        mapView.animate(with: GMSCameraUpdate.fit(bounds, with: UIEdgeInsetsMake(50, 50, 50, 50)))
    }
    
    // MARK: - GMSMapViewDelegate
    
    func mapView(_ mapView: GMSMapView, didTap marker: GMSMarker) -> Bool {
        mapView.moveCamera(GMSCameraUpdate.setCamera(GMSCameraPosition.camera(withLatitude: marker.position.latitude,
                                                                              longitude: marker.position.longitude,
                                                                              zoom: 18)))
        mapView.selectedMarker = marker
        return true
    }
    
    // MARK: - MapPresenter delegate
    
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
    
    
    func maskImage(image: UIImage, withMask maskImage: UIImage) -> UIImage {
        let maskRef = maskImage.cgImage
        let mask = CGImage(
            maskWidth: maskRef!.width,
            height: maskRef!.height,
            bitsPerComponent: maskRef!.bitsPerComponent,
            bitsPerPixel: maskRef!.bitsPerPixel,
            bytesPerRow: maskRef!.bytesPerRow,
            provider: maskRef!.dataProvider!,
            decode: nil,
            shouldInterpolate: false)
        let masked = image.cgImage!.masking(mask!)
        let maskedImage = UIImage(cgImage: masked!)
        // No need to release. Core Foundation objects are automatically memory managed.
        return maskedImage
    }
    
    func addNewPlayerToMap(with image: UIImage, and coordinates: CLLocationCoordinate2D, and info: Dictionary<String,String>) {
        guard let userId = info["id"], let userName = info["name"] else {
            print("Cannot add player marker to map with recieved info")
            return
        }
        if markers[userId] != nil {
            var customImage = image.roundedImageWithBorder(with: 5, and: #colorLiteral(red: 0.5176470588, green: 0.3411764706, blue: 0.6, alpha: 1))
            customImage = customImage?.imageResize(sizeChange: CGSize.init(width: 45, height: 45))
            markers[userId]?.icon = customImage
            markers[userId]?.position = coordinates
            markers[userId]?.title = userName
            print("Recieved player marker is already on the map. Coordinates were updated.")
        }
        else {
            let marker = GMSMarker()
            marker.position = coordinates
            marker.title = userName
            var customImage = image.roundedImageWithBorder(with: 5, and: #colorLiteral(red: 0.5176470588, green: 0.3411764706, blue: 0.6, alpha: 1))
            customImage = customImage?.imageResize(sizeChange: CGSize.init(width: 45, height: 45))
            marker.icon = customImage
            marker.map = mapView
            markers[userId] = marker
            print("New player marker added to the map")
        }
    }
    
    // MARK: - CLLocationManager delegate
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        if let currentLocation = locations.first {
            presenter?.userLocationUpdate(currentLocation)
        }
    }
}
