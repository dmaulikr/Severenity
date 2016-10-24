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
    
    override init() {
        super.init()
        interactor = MapInteractor()
        interactor?.delegate = self
    }
    
    func mapInteractorDidCallPresenter(with data: Dictionary<String,AnyObject>) {
        print("Map Presenter is called from Map Interactor with data: \(data)")
        delegate?.mapPresenterDidCallView(with: data)
        
        let tabBarController = ((UIApplication.shared.delegate as! AppDelegate).window?.rootViewController) as! UITabBarController
        tabBarController.selectedIndex = 2;
    }
    
    func userLocationChange(_ newLocation: CLLocation) {
        print("User changed location, MapPresenter recieved new data.")
        let currentLocationDictionary: [String:String] = ["lat":"\(newLocation.coordinate.latitude)",
                                                          "lng":"\(newLocation.coordinate.longitude)",
                                                          "id":FBSDKAccessToken.current().userID]
        interactor?.processNewUserLocation(with: currentLocationDictionary)
    }
}
