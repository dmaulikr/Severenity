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
    
    override init(style: UITableViewStyle) {
        super.init(style: style)
        presenter = ProfileListPresenter()
        presenter?.delegate = self
        print("ProfileList VIPER module init did complete")
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    // MARK: ProfileList Presenter delegate
    
    func profileListPresenterDidCallView(withData data: [String]) {
        print("ProfileList Presenter did call view")
        dataForList = data
        tableView.reloadData()
    }
    
    // MARK: - Loading view
    
    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.register(UINib(nibName: "ProfileListCell", bundle: nil), forCellReuseIdentifier: "ProfileCellInList")
        
        // Data loading indicator for the UITableView
        let activityIndicatorView = UIActivityIndicatorView(activityIndicatorStyle: UIActivityIndicatorViewStyle.gray)
        activityIndicatorView.color = UIColor.magenta
        tableView.backgroundView = activityIndicatorView
        self.activityIndicatorView = activityIndicatorView
        activityIndicatorView.startAnimating()

        presenter?.provideProfileListData()
    }
    
    // MARK: - Table view delegate methods
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        // probably need to refactor this pyramid of doom with guard statement and get tabs by name, not indexes
        presenter?.profileListCell(selected: indexPath)
        tableView.deselectRow(at: indexPath, animated: true)
    }
    
    override func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        
        if (indexPath as NSIndexPath).row == (tableView.indexPathsForVisibleRows?.last as NSIndexPath?)?.row {
            activityIndicatorView.stopAnimating()
        }
    }

    // MARK: - Table view data source

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
