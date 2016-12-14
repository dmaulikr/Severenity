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
    
    // MARK: Init
    
    override init() {
        super.init()
        WireFrame.sharedInstance.viperInteractors[kMapInteractor] = self
    }
    
    // MARK: Other events
    
    func profileListViewEvent(with data: AnyObject) {
        Log.info(message: "MapInteractor event happened from ProfileListView with data: \(data)", sender: self)
        if let recievedData = data as? Dictionary<String,Any> {
            delegate?.displayPlaceWith(dictionary: recievedData)
        }
    }
    
    // MARK: Service interaction
    
    func processUserLocationUpdate(with dictionary: Dictionary<String,Any>) {
        Log.info(message: "MapInteractor was called from MapPresenter to process new user location", sender: self)
        SocketService.sharedInstance.sendLocationToServer(with: dictionary)
    }
    
    func processNewPlayerLocation(with dictionary: Dictionary<String,Any>) {
        if let lat = dictionary["lat"] as? Double,
            let lng = dictionary["lng"] as? Double,
            let fbUserID = dictionary["id"] as? String {
            FacebookService.sharedInstance.getFBProfilePicture(for: fbUserID, size: .normal) { (image) in
                    FacebookService.sharedInstance.getFBProfileInfo(with: fbUserID, and: { (info) in
                        self.delegate?.displayPlayerWith(picture: image, coordinates: CLLocationCoordinate2DMake(lat, lng), info: info)
                    })
            }
        }
    }
}
