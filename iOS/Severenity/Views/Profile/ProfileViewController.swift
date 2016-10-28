//
//  Tab2ViewController.swift
//  severenityProject
//
//  Created by Yura Yasinskyy on 12.09.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class ProfileViewController: UIViewController, ProfilePresenterDelegate {

    @IBOutlet weak var contentView: UIView!
    @IBOutlet weak var contentModeSwitcher: UISegmentedControl!
    
    private var presenter: ProfilePresenter?
    
    // MARK: - Init
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        presenter = ProfilePresenter()
        presenter?.delegate = self
        print("Profile VIPER module init did complete")
    }
    
    func profilePresenterDidCallView() {
        print("ProfileViewController is called from ProfilePresenter")
        changeContentMode(self)
    }
    
    // MARK:  - Loading view
    
    override func viewDidLoad() {
        super.viewDidLoad()
        presenter?.profileViewEvent()
    }
    
    // MARK: - Managing view presentation
    
    @IBAction func changeContentMode(_ sender: AnyObject) {
        switch contentModeSwitcher.selectedSegmentIndex {
        case 0:
            let listViewController = ProfileListViewController(style: UITableViewStyle.plain)
            addChildViewController(listViewController)
            contentView.subviews.last?.removeFromSuperview()
            contentView.addSubview(listViewController.view)
            listViewController.tableView?.frame = contentView.bounds
        case 1:
            let layout = UICollectionViewFlowLayout()
            layout.itemSize = CGSize(width: 100, height: 50)
            let gridViewController = ProfileGridViewController(collectionViewLayout: layout)
            addChildViewController(gridViewController)
            contentView.subviews.last?.removeFromSuperview()
            contentView.addSubview(gridViewController.view)
            gridViewController.collectionView?.frame = contentView.bounds
        default:
            return
        }
    }
}
