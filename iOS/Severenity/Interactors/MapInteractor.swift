//
//  MapInteractor.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 20.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit
import CoreLocation

class MapInteractor: NSObject {

    weak var delegate: MapInteractorDelegate?
    
    // MARK: - Init
    
    override init() {
        super.init()
        WireFrame.sharedInstance.viperInteractors["MapInteractor"] = self
    }
    
    // MARK: - MapPresenter events
    
    func mapInteractorEvent(with data: AnyObject) {
        print("MapInteractor event happened with data: \(data)")
        if let recievedData = data as? Dictionary<String,AnyObject> {
            delegate?.mapInteractorDidCallPresenter(with: recievedData as Dictionary<String, AnyObject>)
        }
    }
    
    // MARK: - Service interaction
    
    func processNewUserLocation(with dictionary: Dictionary<String,String>) {
        print("MapInteractor was called from MapPresenter to process new user location")
        SocketService.sharedInstance.sendLocationToServer(with: dictionary)
    }
    
    func processNewUser(with dictionary: Dictionary<String,AnyObject>) {

        if let lat = dictionary["lat"] as? CLLocationDegrees,
            let lng = dictionary["lng"] as? CLLocationDegrees,
            let fbId = dictionary["id"] as? String {
            FacebookService.sharedInstance.getFBProfilePicture(with: fbId) { (image) in
                self.delegate?.addNewUserToMap(with: image, and: CLLocationCoordinate2DMake(lat, lng))
            }
        }
    }
}
