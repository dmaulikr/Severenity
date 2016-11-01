//
//  ListViewController.swift
//  severenityProject
//
//  Created by Yura Yasinskyy on 12.09.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class ProfileListViewController: UITableViewController, ProfileListPresenterDelegate {
    
    private var presenter: ProfileListPresenter?
    private var dataForList = [String]()
    weak var activityIndicatorView: UIActivityIndicatorView!
    
    // MARK: - Init
    
    override init(style: UITableViewStyle) {
        super.init(style: style)
        presenter = ProfileListPresenter()
        presenter?.delegate = self
        print("ProfileList VIPER module init did complete")
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    // MARK: - Loading view
    
    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.register(UINib(nibName: "ProfileListCell", bundle: nil), forCellReuseIdentifier: "ProfileCellInList")
        tableView.backgroundColor = UIColor.black
        tableView.separatorColor = #colorLiteral(red: 0.5176470588, green: 0.3411764706, blue: 0.6, alpha: 1)
        
        startActivityIndicator(location: CGPoint.init(x: self.view.frame.width/2, y: self.view.frame.height/2 - 100), view: tableView)
        
        presenter?.provideProfileListData()
    }
    
    // MARK: ProfileListPresenter delegate
    
    func profileListPresenterDidCallView(withData data: [String]) {
        print("ProfileListPresenter did call ProfileListViewController")
        dataForList = data
        tableView.reloadData()
    }
    
    // MARK: - UITableView delegate
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        // probably need to refactor this pyramid of doom with guard statement and get tabs by name, not indexes
        presenter?.profileListCell(selected: indexPath)
        tableView.deselectRow(at: indexPath, animated: true)
    }
    
    override func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        if (indexPath as NSIndexPath).row == (tableView.indexPathsForVisibleRows?.last as NSIndexPath?)?.row {
            stopActivityIndicator(view: tableView)
        }
    }

    // MARK: - UITableView data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return dataForList.count
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        var cell: UITableViewCell
        if let c = tableView.dequeueReusableCell(withIdentifier: "ProfileCellInList", for: indexPath) as? ProfileListCell {
            c.listCellTitle.text = dataForList[(indexPath as NSIndexPath).row]
            cell = c
        } else {
            cell = tableView.dequeueReusableCell(withIdentifier: "ProfileCellInList", for: indexPath)
        }
        return cell
    }
}
