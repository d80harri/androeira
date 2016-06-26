package org.d80harri.androeira.analyzer.filter;

import com.airhacks.afterburner.views.FXMLView;
import org.d80harri.androeira.analyzer.Kickstarter;

/**
 * Created by d80harri on 25.06.16.
 */
public class FilterChainBuilderView extends FXMLView{

    public static void main(String[] args) {
        new Kickstarter().start(FilterChainBuilderView.class);
    }
}
