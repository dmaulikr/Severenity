//
//  Tab2ViewController.swift
//  severenityProject
//
//  Created by Yura Yasinskyy on 12.09.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class Tab2ViewController: UIViewController {
    

    @IBOutlet weak var contentView: UIView!
    @IBOutlet weak var contentModeSwitcher: UISegmentedControl!
    
    @IBAction func changeContentMode(sender: AnyObject) {
        
        switch contentModeSwitcher.selectedSegmentIndex {
        case 0:
            let listViewController = ListViewController()
            self.addChildViewController(listViewController)
            contentView.subviews.last?.removeFromSuperview()
            contentView.addSubview(listViewController.view)
            listViewController.tableView?.frame = contentView.bounds
            
        case 1:
            let layout = UICollectionViewFlowLayout()
            layout.itemSize = CGSizeMake(100, 50)
            let gridViewController = GridViewController(collectionViewLayout: layout)
            self.addChildViewController(gridViewController)
            contentView.subviews.last?.removeFromSuperview()
            contentView.addSubview(gridViewController.view)
            gridViewController.collectionView?.frame = contentView.bounds
        default:
            return
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        changeContentMode(self)
    }


}
