//
//  TodayViewController.swift
//  SeverenityWidget
//
//  Created by Yuriy Yasinskyy on 30.11.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit
import NotificationCenter

class TodayViewController: UIViewController, NCWidgetProviding {
    
    @IBOutlet weak var profilePicture: UIImageView!
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var levelLabel: UILabel!
    
    // MARK: Loading view
    
    override func viewDidLoad() {
        super.viewDidLoad()
        preferredContentSize = CGSize(width: 320, height: 100)
        // Do any additional setup after loading the view from its nib.
    }
    
    // MARK: Updating widget
    
    func widgetPerformUpdate(completionHandler: (@escaping (NCUpdateResult) -> Void)) {
        // Perform any setup necessary in order to update the view.
        
        // If an error is encountered, use NCUpdateResult.Failed
        // If there's no update required, use NCUpdateResult.NoData
        // If there's an update, use NCUpdateResult.NewData
        self.retrieveSharedData()
        completionHandler(NCUpdateResult.newData)
    }
    
}

extension TodayViewController {
    
    /// Loads FB profile data from UserDefaults shared by containing application
    func retrieveSharedData() {
        let userDefaults = UserDefaults(suiteName: "group.severenity.DataSharing")
        if let data = userDefaults?.dictionary(forKey: "profileData"), let name = data["name"] as? String,
            let imageData = userDefaults?.object(forKey: "profilePicture") as? Data {
            nameLabel.text = name
            if nameLabel.text == "Oleg Novosad" {
                levelLabel.text = levelLabel.text?.appending("100501. Father of Severenity. God of software development. Has infinite powers.")
            } else {
                levelLabel.text = levelLabel.text?.appending("1. Unknown hero.")
            }
            profilePicture.image = UIImage(data: imageData)?.roundedImageWithBorder(with: 4, and: #colorLiteral(red: 0.5176470588, green: 0.3411764706, blue: 0.6, alpha: 1))
        }
    }
    
}

extension UIImage {
    
    func roundedImageWithBorder(with width: CGFloat, and color: UIColor) -> UIImage? {
        let square = CGSize(width: min(size.width, size.height) + width * 2, height: min(size.width, size.height) + width * 2)
        let imageView = UIImageView(frame: CGRect(origin: CGPoint(x: 0, y: 0), size: square))
        imageView.contentMode = .center
        imageView.image = self
        imageView.layer.cornerRadius = square.width/2
        imageView.layer.masksToBounds = true
        imageView.layer.borderWidth = width
        imageView.layer.borderColor = color.cgColor
        UIGraphicsBeginImageContextWithOptions(imageView.bounds.size, false, scale)
        guard let context = UIGraphicsGetCurrentContext() else { return nil }
        imageView.layer.render(in: context)
        let result = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return result
    }
    
}
