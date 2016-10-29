//
//  MessageView.swift
//  Severenity
//
//  Created by Yura Yasinskyy on 29.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class MessageView: UITableViewCell {
    
    @IBOutlet weak var profilePicture: UIImageView!
    @IBOutlet weak var messageText: UITextView!
    @IBOutlet weak var infoLabel: UILabel!

    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
