package com.gwtproject.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Sorting implements EntryPoint {

  public void onModuleLoad() {
      startApp();
  }

  private void startApp() {
    RootPanel.get().add(new Main());
  }
}
