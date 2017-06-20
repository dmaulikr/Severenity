package com.severenity.view.fragments;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.severenity.App;
import com.severenity.R;
import com.severenity.engine.adapters.ShopItemsAdapter;
import com.severenity.entity.ShopItem;
import com.severenity.utils.shop.IabBroadcastReceiver;
import com.severenity.utils.shop.IabHelper;
import com.severenity.utils.shop.IabResult;
import com.severenity.utils.shop.Inventory;
import com.severenity.utils.shop.Purchase;
import com.severenity.utils.common.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Example game using in-app billing version 3.
 *
 * Before attempting to run this sample, please read the README file. It
 * contains important information on how to set up this project.
 *
 * All the game-specific logic is implemented here in MainActivity, while the
 * general-purpose boilerplate that can be reused in any app is provided in the
 * classes in the util/ subdirectory. When implementing your own application,
 * you can copy over util/*.java to make use of those utility classes.
 *
 * This game is a simple "driving" game where the player can buy gas
 * and drive. The car has a tank which stores gas. When the player purchases
 * gas, the tank fills up (1/4 tank at a time). When the player drives, the gas
 * in the tank diminishes (also 1/4 tank at a time).
 *
 * The user can also purchase a "premium upgrade" that gives them a red car
 * instead of the standard blue one (exciting!).
 *
 * The user can also purchase a subscription ("infinite gas") that allows them
 * to drive without using up any gas while that subscription is active.
 *
 * It's important to note the consumption mechanics for each item.
 *
 * PREMIUM: the item is purchased and NEVER consumed. So, after the original
 * purchase, the player will always own that item. The application knows to
 * display the red car instead of the blue one because it queries whether
 * the premium "item" is owned or not.
 *
 * INFINITE GAS: this is a subscription, and subscriptions can't be consumed.
 *
 * GAS: when gas is purchased, the "gas" item is then owned. We consume it
 * when we apply that item's effects to our app's world, which to us means
 * filling up 1/4 of the tank. This happens immediately after purchase!
 * It's at this point (and not when the user drives) that the "gas"
 * item is CONSUMED. Consumption should always happen when your game
 * world was safely updated to apply the effect of the purchase. So,
 * in an example scenario:
 *
 * BEFORE:      tank at 1/2
 * ON PURCHASE: tank at 1/2, "gas" item is owned
 * IMMEDIATELY: "gas" is consumed, tank goes to 3/4
 * AFTER:       tank at 3/4, "gas" item NOT owned any more
 *
 * Another important point to notice is that it may so happen that
 * the application crashed (or anything else happened) after the user
 * purchased the "gas" item, but before it was consumed. That's why,
 * on startup, we check if we own the "gas" item, and, if so,
 * we have to apply its effects to our world and consume it. This
 * is also very important!
 */
public class ShopFragment extends Fragment implements IabBroadcastReceiver.IabBroadcastListener, ShopItemsAdapter.OnShopItemClickListener {
    private RecyclerView rvShopItemsList;
    private ShopItem.ShopItemType currentItemUnderPurchase;

    // Does the user have an active subscription to the year tickets plan?
    boolean mSubscribedToAnnualTickets = false;

    // Will the subscription auto-renew?
    boolean mAutoRenewEnabled = false;

    // Tracks the currently owned yearly tickets SKU, and the options in the Manage dialog
    String mAnnualTicketsSku = "";

    // SKUs for our products: consumable quest tip, quest ticket and credits
    static final String SKU_QUEST_TICKET = "quest_ticket";
    static final String SKU_QUEST_TIP = "quest_tip";
    static final String SKU_CREDITS = "credits";

    // SKU for year tickets subscription
    static final String SKU_ANNUAL_TICKETS = "all_quests_subscription";

    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;

    // How many tickets team may have at max.
    static final int TICKETS_MAX = 24;
    // How many tips you may have at max.
    static final int TIPS_MAX = 3;

    // The helper object
    IabHelper mHelper;

    // Provides purchase notification while this app is running
    IabBroadcastReceiver mBroadcastReceiver;

    public ShopFragment() {
        // Required empty public constructor
    }

    private List<ShopItem> createMockListData() {
        List<ShopItem> list = new ArrayList<>();
        list.add(new ShopItem(ShopItem.ShopItemType.credits, getResources().getString(R.string.shop_item_title_credits), R.drawable.shop_item_credits, getResources().getString(R.string.shop_item_description_credits), 0, 1.99));
        list.add(new ShopItem(ShopItem.ShopItemType.quest_tip, getResources().getString(R.string.shop_item_title_quest_tip), R.drawable.shop_item_tip, getResources().getString(R.string.shop_item_description_quest_tip), 50, 0.99));
        list.add(new ShopItem(ShopItem.ShopItemType.quest_ticket, getResources().getString(R.string.shop_item_title_quest_ticket), R.drawable.shop_item_ticket, getResources().getString(R.string.shop_item_description_quest_ticket), 0, 3.99));
        return list;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shop, container, false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        rvShopItemsList = (RecyclerView) view.findViewById(R.id.rvShopItems);
        rvShopItemsList.setLayoutManager(gridLayoutManager);
        ShopItemsAdapter adapter = new ShopItemsAdapter(createMockListData(), this);
        rvShopItemsList.setAdapter(adapter);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* base64EncodedPublicKey should be YOUR APPLICATION'S PUBLIC KEY
         * (that you got from the Google Play developer console). This is not your
         * developer public key, it's the *app-specific* public key.
         *
         * Instead of just storing the entire literal string here embedded in the
         * program,  construct the key at runtime from pieces or
         * use bit manipulation (for example, XOR with some other string) to hide
         * the actual key.  The key itself is not secret information, but we don't
         * want to make it easy for an attacker to replace the public key with one
         * of their own and then fake messages from the server.
         */
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwDo5cnWibZEcBnIja1rNfxObBsEQBHbZsH2h3i6oKysme4X0hlLbaGjsbiH7N2gLtrPajgjbylwF3UbQH2T6OuWNqt/a63olNjQPoGjtUWdQOIelM1rSKeS/mt9eswYm0rs6hYVozOTxiDG0nTmAYl4t6ZQeMAQlrTB5anauNaptzTm0VAs777TMvjDCClFaw9B8ILNM8lK+SCtMYCBeIWvJMoMWCeXSrya1sa6+8eLVaSx52rzwBvzWZLGr4g+xLbSbeZb53U5N4z4UJrrMgRRSUELu5R32A3dJc17XdUcoGwk4u6HL0jwB6iySqKvltA4k7pOlWJZijYQ/aRjqdwIDAQAB";

        // Create the helper, passing it our context and the public key to verify signatures with
        Log.d(Constants.TAG, "Creating IAB helper.");
        mHelper = new IabHelper(getActivity(), base64EncodedPublicKey);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.d(Constants.TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(Constants.TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // Important: Dynamically register for broadcast messages about updated purchases.
                // We register the receiver here instead of as a <receiver> in the Manifest
                // because we always call getPurchases() at startup, so therefore we can ignore
                // any broadcasts sent while the app isn't running.
                // Note: registering this listener in an Activity is a bad idea, but is done here
                // because this is a SAMPLE. Regardless, the receiver must be registered after
                // IabHelper is setup, but before first call to getPurchases().
                mBroadcastReceiver = new IabBroadcastReceiver(ShopFragment.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                App.getLocalBroadcastManager().registerReceiver(mBroadcastReceiver, broadcastFilter);

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(Constants.TAG, "Setup successful. Querying inventory.");
                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error querying inventory. Another async operation in progress.");
                }
            }
        });
    }

    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(Constants.TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) {
                return;
            }

            // Is it a failure?
            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                return;
            }

            Log.d(Constants.TAG, "Query inventory was successful.");

            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */

            // First find out which subscription is auto renewing
            Purchase yearlyTickets = inventory.getPurchase(SKU_ANNUAL_TICKETS);
            if (yearlyTickets != null && yearlyTickets.isAutoRenewing()) {
                mAnnualTicketsSku = SKU_ANNUAL_TICKETS;
                mAutoRenewEnabled = true;
            } else {
                mAnnualTicketsSku = "";
                mAutoRenewEnabled = false;
            }

            // The user is subscribed if either subscription exists, even if neither is auto
            // renewing
            mSubscribedToAnnualTickets = yearlyTickets != null && verifyDeveloperPayload(yearlyTickets);
            Log.d(Constants.TAG, "User " + (mSubscribedToAnnualTickets ? "HAS" : "DOES NOT HAVE")
                    + " year tickets subscription.");

            if (mSubscribedToAnnualTickets) {
                App.getUserManager().updateCurrentUser("tickets", TICKETS_MAX);
            }

            // Check for ticket delivery -- if we own ticket, we should fill up the tickets immediately
            Purchase ticketPurchase = inventory.getPurchase(SKU_QUEST_TICKET);
            if (ticketPurchase != null && verifyDeveloperPayload(ticketPurchase)) {
                Log.d(Constants.TAG, "We have ticket. Consuming it.");
                try {
                    mHelper.consumeAsync(inventory.getPurchase(SKU_QUEST_TICKET), mConsumeFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error consuming ticket. Another async operation in progress.");
                }
                return;
            }

            // Check for credits delivery -- if we own credits, we should add credits to user immediately
            Purchase creditsPurchase = inventory.getPurchase(SKU_CREDITS);
            if (creditsPurchase != null && verifyDeveloperPayload(creditsPurchase)) {
                Log.d(Constants.TAG, "We have credits. Consuming it.");
                try {
                    mHelper.consumeAsync(inventory.getPurchase(SKU_CREDITS), mConsumeFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error consuming credits. Another async operation in progress.");
                }
                return;
            }

            // Check for quest tip delivery -- if we own tip, we should add tips to user immediately
            Purchase tipPurchase = inventory.getPurchase(SKU_QUEST_TIP);
            if (tipPurchase != null && verifyDeveloperPayload(tipPurchase)) {
                Log.d(Constants.TAG, "We have tip. Consuming it.");
                try {
                    mHelper.consumeAsync(inventory.getPurchase(SKU_QUEST_TIP), mConsumeFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error consuming tip. Another async operation in progress.");
                }
                return;
            }

            Log.d(Constants.TAG, "Initial inventory query finished; enabling main UI.");
        }
    };

    @Override
    public void receivedBroadcast() {
        // Received a broadcast notification that the inventory of items has changed
        Log.d(Constants.TAG, "Received broadcast notification. Querying inventory.");
        try {
            mHelper.queryInventoryAsync(mGotInventoryListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            complain("Error querying inventory. Another async operation in progress.");
        }
    }

    // User clicked the "Purchase" on quest ticket
    public void onPurchaseQuestTicketClicked() {
        Log.d(Constants.TAG, "Buy ticket button clicked.");

        if (mSubscribedToAnnualTickets) {
            complain("No need! You're subscribed to all quests this year. Isn't that awesome?");
            return;
        }

        if (App.getUserManager().getCurrentUser().getTickets() >= TICKETS_MAX) {
            complain("You have tickets for a year. Just use them! :-)");
            return;
        }

        // launch the gas purchase UI flow.
        // We will be notified of completion via mPurchaseFinishedListener
        Log.d(Constants.TAG, "Launching purchase flow for quest ticket.");

        /* TODO: for security, generate your payload here for verification. See the comments on
         *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
         *        an empty string, but on a production app you should carefully generate this. */
        String payload = App.getUserManager().getCurrentUser().getId() + "_purchase_" + SKU_QUEST_TICKET;

        try {
            mHelper.launchPurchaseFlow(getActivity(), SKU_QUEST_TICKET, RC_REQUEST,
                    mPurchaseFinishedListener, payload);
        } catch (IabHelper.IabAsyncInProgressException e) {
            complain("Error launching purchase flow. Another async operation in progress.");
        }
    }

    // User clicked the "Purchase" on quest tip
    public void onPurchaseQuestTipClicked() {
        Log.d(Constants.TAG, "Purchase quest tip button clicked; launching purchase flow for quest tip.");

        /* TODO: for security, generate your payload here for verification. See the comments on
         *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
         *        an empty string, but on a production app you should carefully generate this. */
        String payload = App.getUserManager().getCurrentUser().getId() + "_purchase_" + SKU_QUEST_TIP;

        try {
            mHelper.launchPurchaseFlow(getActivity(), SKU_QUEST_TIP, RC_REQUEST,
                    mPurchaseFinishedListener, payload);
        } catch (IabHelper.IabAsyncInProgressException e) {
            complain("Error launching purchase flow. Another async operation in progress.");
        }
    }

    // User clicked the "Purchase" on quest tip
    public void onPurchaseCreditsClicked() {
        Log.d(Constants.TAG, "Purchase credits button clicked; launching purchase flow for credits.");

        /* TODO: for security, generate your payload here for verification. See the comments on
         *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
         *        an empty string, but on a production app you should carefully generate this. */
        String payload = App.getUserManager().getCurrentUser().getId() + "_purchase_" + SKU_CREDITS;

        try {
            mHelper.launchPurchaseFlow(getActivity(), SKU_CREDITS, RC_REQUEST,
                    mPurchaseFinishedListener, payload);
        } catch (IabHelper.IabAsyncInProgressException e) {
            complain("Error launching purchase flow. Another async operation in progress.");
        }
    }

    // "Subscribe to all team quests this year" button clicked. Explain to user, then start purchase
    // flow for subscription.
    public void onYearlyQuestsPurchaseClicked() {
        if (!mHelper.subscriptionsSupported()) {
            complain("Subscriptions not supported on your device yet. Sorry!");
            return;
        }

        /* TODO: for security, generate your payload here for verification. See the comments on
             *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
             *        an empty string, but on a production app you should carefully generate
             *        this. */
        String payload = App.getUserManager().getCurrentUser().getId() + "_purchase_" + SKU_ANNUAL_TICKETS;

        Log.d(Constants.TAG, "Launching purchase flow for annual ticket subscription.");
        try {
            mHelper.launchPurchaseFlow(getActivity(), SKU_ANNUAL_TICKETS, IabHelper.ITEM_TYPE_SUBS, null, RC_REQUEST, mPurchaseFinishedListener, payload);
        } catch (IabHelper.IabAsyncInProgressException e) {
            complain("Error launching purchase flow. Another async operation in progress.");
        }
    }

    /** Verifies the developer payload of a purchase. */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

        return true;
    }

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(Constants.TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) {
                return;
            }

            if (result.isFailure()) {
                complain("Error purchasing: " + result);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain("Error purchasing. Authenticity verification failed.");
                return;
            }

            Log.d(Constants.TAG, "Purchase successful.");

            switch (purchase.getSku()) {
                case SKU_QUEST_TICKET:
                    // bought 1 ticket. So consume it.
                    Log.d(Constants.TAG, "Purchase is ticket. Starting ticket consumption.");
                    try {
                        mHelper.consumeAsync(purchase, mConsumeFinishedListener);
                    } catch (IabHelper.IabAsyncInProgressException e) {
                        complain("Error consuming ticket. Another async operation in progress.");
                    }
                    break;
                case SKU_QUEST_TIP:
                    // bought the quest tip!
                    Log.d(Constants.TAG, "Purchase is quest tip. Starting tip consumption.");
                    try {
                        mHelper.consumeAsync(purchase, mConsumeFinishedListener);
                    } catch (IabHelper.IabAsyncInProgressException e) {
                        complain("Error consuming credits. Another async operation in progress.");
                    }

                    // TODO: Reflect on UI changes according to purchases
                    break;
                case SKU_CREDITS:
                    // bought the credits!
                    Log.d(Constants.TAG, "Purchase is credits. Starting credits consumption.");
                    try {
                        mHelper.consumeAsync(purchase, mConsumeFinishedListener);
                    } catch (IabHelper.IabAsyncInProgressException e) {
                        complain("Error consuming credits. Another async operation in progress.");
                    }
                    break;
                case SKU_ANNUAL_TICKETS:
                    // bought the infinite gas subscription
                    Log.d(Constants.TAG, "Annual tickets subscription purchased.");
                    alert("Thank you for subscribing to annual tickets! You can attend all team quests next year!");
                    mSubscribedToAnnualTickets = true;
                    mAutoRenewEnabled = purchase.isAutoRenewing();
                    mAnnualTicketsSku = purchase.getSku();
                    App.getUserManager().updateCurrentUser("tickets", TICKETS_MAX);
                    break;
            }
        }
    };

    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(Constants.TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) {
                return;
            }

            if (result.isFailure()) {
                complain("Error while consuming: " + result);
                return;
            }

            switch (purchase.getSku()) {
                case SKU_QUEST_TICKET:
                    Log.d(Constants.TAG, "Consumption successful. Provisioning.");

                    App.getUserManager().updateCurrentUser("tickets", 1);
                    break;
                case SKU_QUEST_TIP:
                    Log.d(Constants.TAG, "Consumption successful. Provisioning.");

                    App.getUserManager().updateCurrentUser("tips", 1);
                    break;
                case SKU_CREDITS:
                    Log.d(Constants.TAG, "Consumption successful. Provisioning.");

                    App.getUserManager().updateCurrentUser("credits", 100);
                    break;
            }

            Log.d(Constants.TAG, "End consumption flow.");
        }
    };

    // We're being destroyed. It's important to dispose of the helper here!
    @Override
    public void onDestroy() {
        super.onDestroy();

        // very important:
        if (mBroadcastReceiver != null) {
            App.getLocalBroadcastManager().unregisterReceiver(mBroadcastReceiver);
            App.getLocalBroadcastManager().unregisterReceiver(updateUIInfoReceiver);
        }

        // very important:
        Log.d(Constants.TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper.disposeWhenFinished();
            mHelper = null;
        }
    }

    void complain(String message) {
        Log.e(Constants.TAG, "**** TrivialDrive Error: " + message);
        alert("Error: " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(getActivity());
        bld.setMessage(message);
        bld.setNeutralButton(getResources().getString(R.string.ok), null);
        Log.d(Constants.TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }

    @Override
    public void onShopItemClicked(ShopItem item) {
        currentItemUnderPurchase = item.getType();
        switch (item.getType()) {
            case credits:
                onPurchaseCreditsClicked();
                break;
            case quest_tip:
                onPurchaseQuestTipClicked();
                break;
            case quest_ticket:
                onPurchaseQuestTicketClicked();
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        App.getLocalBroadcastManager().registerReceiver(
                updateUIInfoReceiver, new IntentFilter(Constants.INTENT_FILTER_UPDATE_UI));
    }

    public IabHelper getPurchaseHelper() {
        return mHelper;
    }

    private BroadcastReceiver updateUIInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (currentItemUnderPurchase == null) {
                return;
            }

            switch (currentItemUnderPurchase) {
                case credits:
                    alert("Thank you for purchasing credits! Now use them for in-game activities");
                    break;
                case quest_ticket:
                    alert("You own " + (App.getUserManager().getCurrentUser().getTickets() == 1 ? "1 ticket!" : String.valueOf(App.getUserManager().getCurrentUser().getTickets()) + " tickets!") + "\nNow gather team and take a quest!");
                    break;
                case quest_tip:
                    alert("You own " + (App.getUserManager().getCurrentUser().getTips() == 1 ? "1 tip!" : String.valueOf(App.getUserManager().getCurrentUser().getTips()) + " tips!" ) + "\nTake advantage of tip help on next quest!");
                    break;
            }

            currentItemUnderPurchase = null;
        }
    };
}