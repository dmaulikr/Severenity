//
//  Extensions.swift
//  Severenity
//
//  Created by Yura Yasinskyy on 28.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

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
    func imageResize (sizeChange:CGSize)-> UIImage{
        let hasAlpha = true
        let scale: CGFloat = 0.0 // Use scale factor of main screen
        UIGraphicsBeginImageContextWithOptions(sizeChange, !hasAlpha, scale)
        self.draw(in: CGRect(origin: CGPoint.zero, size: sizeChange))
        let scaledImage = UIGraphicsGetImageFromCurrentImageContext()
        return scaledImage!
    }
}

extension UIView {
    class func loadFromNibNamed(nibNamed: String, bundle : Bundle? = nil) -> UIView? {
        return UINib(
            nibName: nibNamed,
            bundle: bundle
            ).instantiate(withOwner: nil, options: nil)[0] as? UIView
    }
}

extension UIViewController {
    
    var activityIndicatorTag: Int { return 999998 }
    var coverViewTag: Int { return 999999 }
    
    func startActivityIndicator(
        style: UIActivityIndicatorViewStyle = .whiteLarge,
        location: CGPoint? = nil) {
        let loc = location ?? self.view.center
        DispatchQueue.main.async(execute: {
            let activityIndicator = UIActivityIndicatorView(activityIndicatorStyle: style)
            activityIndicator.color = UIColor.magenta
            activityIndicator.tag = self.activityIndicatorTag
            activityIndicator.center = loc
            activityIndicator.hidesWhenStopped = true
            activityIndicator.startAnimating()
            let coverView = UIView.init(frame: self.view.frame)
            coverView.backgroundColor = self.view.backgroundColor
            coverView.tag = self.coverViewTag
            self.view.addSubview(coverView)
            self.view.addSubview(activityIndicator)
        })
    }
    
    func stopActivityIndicator() {
        DispatchQueue.main.async(execute: {
            if let activityIndicator = self.view.subviews.filter(
                { $0.tag == self.activityIndicatorTag}).first as? UIActivityIndicatorView {
                activityIndicator.stopAnimating()
                activityIndicator.removeFromSuperview()
            }
            if let coverView = self.view.subviews.filter(
                { $0.tag == self.coverViewTag}).first {
                coverView.removeFromSuperview()
            }
        })
    }
}

extension NSObject {
    var currentTimeStamp: String {
        return "\(NSDate().timeIntervalSince1970 * 1000)"
    }
}
