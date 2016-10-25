//
//  TabBarController.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 21.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class TabBarController: UITabBarController {
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        print("TabBar init did complete")
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        print("TabBarController did load")
        // Do any additional setup after loading the view.
    }
}
