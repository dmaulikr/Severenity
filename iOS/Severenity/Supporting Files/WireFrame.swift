//
//  WireFrame.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 21.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class WireFrame: NSObject {

    static let sharedInstance = WireFrame()
    var viperInteractors = [String:AnyObject]()
    
    private override init() {
        super.init()
        print("WireFrame shared instance init did complete")
    }
    
}
