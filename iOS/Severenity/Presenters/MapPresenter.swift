//
//  MapPresenter.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 20.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit
import CoreLocation
import FBSDKLoginKit

class MapPresenter: NSObject, MapInteractorDelegate {
    
    private var interactor: MapInteractor?
    weak var delegate: MapPresenterDelegate?
    
    // MARK: - Init
    
    override init() {
        super.init()
        interactor = MapInteractor()
        interactor?.delegate = self
    }
    
    // MARK: - MapViewController events
    
    func userLocationUpdate(_ newLocation: CLLocation) {
        print("User changed location, MapPresenter recieved new data.")
        let currentLocationDictionary: [String:Any] = ["lat":newLocation.coordinate.latitude,
            "lng":newLocation.coordinate.longitude,
            "id":(FBSDKAccessToken.current().userID)!]
        interactor?.processUserLocationUpdate(with: currentLocationDictionary)
    }
    
    // MARK: - MapInteractor delegate
    
    func displayPlace(with data: Dictionary<String,Any>) {
        print("MapPresenter is called from MapInteractor with data: \(data)")
        delegate?.addNewPlaceToMap(with: data)

        let tabBarController = ((UIApplication.shared.delegate as? AppDelegate)?.window?.rootViewController) as? UITabBarController
        tabBarController?.selectedIndex = 2;
    }
    
    func displayPlayer(with picture: UIImage, and coordinates: CLLocationCoordinate2D, and info: Dictionary<String,String>) {
        delegate?.addNewPlayerToMap(with: picture, and: coordinates, and: info)
    }
}
