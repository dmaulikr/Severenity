//
//  MapPresenter.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 20.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

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
    
    func mapViewEvent() {
        print("User interacted with Map View, Map Presenter responds.")
        interactor?.mapPresenterEvent()
    }
}
