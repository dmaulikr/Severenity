//
//  Logger.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 24.11.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import Foundation

class Log: NSObject {
    
    class func info(message: String, sender: String = "") {
        print("INFO: \(message) FROM: \(sender) ")
    }
    
    class func error(message: String, sender: String = "") {
        print("ERROR: \(message) FROM: \(sender) ")
    }
    
}
