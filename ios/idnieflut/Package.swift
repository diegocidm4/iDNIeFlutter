// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "idnieflut",
    platforms: [
        .iOS(.v16)
    ],
    products: [
        .library(
            name: "idnieflut",
            targets: ["idnieflut"]
        )
    ],
    dependencies: [
        // Conservamos las URLs de GitHub del SPM anterior, ya que SPM no lee el registro de CocoaPods
        .package(url: "https://github.com/diegocidm4/iDNIe.git", branch: "main"),
        .package(url: "https://github.com/krzyzanowskim/OpenSSL.git", exact: "1.1.2301"),
        .package(name: "FlutterFramework", path: "../FlutterFramework")
    ],
    targets: [
        .target(
            name: "idnieflut",
            dependencies: [
                .product(name: "iDNIe", package: "iDNIe"),
                .product(name: "OpenSSL", package: "OpenSSL"),
                .product(name: "FlutterFramework", package: "FlutterFramework")
            ]
        )
    ]
)