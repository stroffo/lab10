package it.unibo.mvc.controller;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

import it.unibo.mvc.DrawResult;
import it.unibo.mvc.model.DrawNumber;
import it.unibo.mvc.model.DrawNumberImpl;
import it.unibo.mvc.util.ConfigurationManager;
import it.unibo.mvc.view.DrawNumberView;
import it.unibo.mvc.view.DrawNumberViewImpl;
import it.unibo.mvc.view.PrintStreamView;

/**
 */
public final class DrawNumberApp implements DrawNumberViewObserver {
    private final DrawNumber model;
    private final List<DrawNumberView> views;
    private final ConfigurationManager configMgr;

    /**
     * @param views
     *            the views to attach
     */
    public DrawNumberApp(final DrawNumberView... views) {
        /*
         * Side-effect proof
         */
        configMgr = new ConfigurationManager();

        this.views = Arrays.asList(Arrays.copyOf(views, views.length));
        for (final DrawNumberView view: views) {
            view.setObserver(this);
            view.start();
        }
    
        try {
            configMgr.loadConfiguration();
        } catch (final Exception e) {
            for (final DrawNumberView view: views) {
                view.displayError("Error loading config " + e.getMessage() + ". Exiting...");
            }
            this.quit();
        }

        var config = configMgr.getloadedConfiguration();
        if (config == null) {
            for (final DrawNumberView view: views) {
                view.displayError("Could not get loaded configuration. Exiting...");
            }
            this.quit();
        }

        if (!config.isConsistent()) {
            for (final DrawNumberView view: views) {
                view.displayError("Invalid configuration! Exiting...");
            }
            this.quit();
        }

        this.model = new DrawNumberImpl(
            config.getMin(), 
            config.getMax(), 
            config.getAttempts()
        );
    }

    @Override
    public void newAttempt(final int n) {
        try {
            final DrawResult result = model.attempt(n);
            for (final DrawNumberView view: views) {
                view.result(result);
            }
        } catch (IllegalArgumentException e) {
            for (final DrawNumberView view: views) {
                view.numberIncorrect();
            }
        }
    }

    @Override
    public void resetGame() {
        this.model.reset();
    }

    @Override
    public void quit() {
        /*
         * A bit harsh. A good application should configure the graphics to exit by
         * natural termination when closing is hit. To do things more cleanly, attention
         * should be paid to alive threads, as the application would continue to persist
         * until the last thread terminates.
         */
        System.exit(0);
    }

    /**
     * @param args
     *            ignored
     * @throws FileNotFoundException 
     */
    public static void main(final String... args) throws FileNotFoundException {
        new DrawNumberApp(
            new DrawNumberViewImpl(), 
            new PrintStreamView(System.out),
            new PrintStreamView(
                // Points to home folder ... /draw_number_app.log
                System.getProperty("user.home")
                + System.getProperty("file.separator")
                + DrawNumberApp.class.getSimpleName()
                    .replaceAll("([a-z])([A-Z])", "$1_$2")
                    .toLowerCase()
                + ".log"
            )
        );
    }
}
