'use strict';

var React = require('react-native');
// var RCTDeviceEventEmitter = require('RCTDeviceEventEmitter');

var {NativeModules, DeviceEventEmitter} = React;

module.exports = {
  getOrientation: function(callback) {
    NativeModules.OrientationListener.getOrientation((orientation, device) => {
      callback({
        orientation,
        device
      });
    });
  },
  addListener: function(callback) {
    return DeviceEventEmitter.addListener(
      'orientationDidChange', callback
    );
  },
  removeListener: function(listener) {
    DeviceEventEmitter.removeListener(
      'orientationDidChange', listener
    );
  }
}
