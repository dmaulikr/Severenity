//
//  MapInteractor.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 20.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class MapInteractor: NSObject {

    weak var delegate: MapInteractorDelegate?
    
    override init() {
        super.init()
        WireFrame.sharedInstance.viperInteractors["MapInteractor"] = self
    }
    
    func processNewUserLocation(with dictionary: Dictionary<String,String>) {
        print("Map Interactor was called from Map Presenter to process new user location")
        SocketService.sharedInstance.sendLocationToServer(with: dictionary)
    }
    
    func mapInteractorEvent(with data: AnyObject) {
        print("Map Interactor event happened with data: \(data)")
        delegate?.mapInteractorDidCallPresenter(with: data as! Dictionary)
    }
}
