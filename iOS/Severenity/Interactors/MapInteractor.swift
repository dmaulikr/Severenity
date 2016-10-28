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
        delegate?.mapInteractorDidCallPresenter(with: data as! Dictionary)
    }
    
    // MARK: - Service interaction
    
    func processNewUserLocation(with dictionary: Dictionary<String,String>) {
        print("MapInteractor was called from Map Presenter to process new user location")
        SocketService.sharedInstance.sendLocationToServer(with: dictionary)
    }
    
    func processNewUser(with dictionary: Dictionary<String,AnyObject>) {
        FacebookService.sharedInstance.getFBProfilePicture(with: dictionary["id"] as! String) { (image) in
            self.delegate?.addNewUserToMap(with: image, and: CLLocationCoordinate2DMake(dictionary["lat"] as! CLLocationDegrees, dictionary["lng"] as! CLLocationDegrees))
        }
    }
}
