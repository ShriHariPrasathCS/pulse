package com.pulseplus.navigation;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.armor.fontlib.CTextView;
import com.pulseplus.R;
import com.pulseplus.global.Global;

public class DrawerFragment extends Fragment {

    public static final int HOME = 0;
    public static final int MY_ACCOUNT = 1;
    public static final int ORDER_HISTORY = 2;
    public static final int PROMOTION = 3;
    public static final int ABOUT = 4;
    public static final int HELP = 5;
    public static final int FAQ = 6;
    public static final int CONTACT = 7;

    private LinearLayout homeLayout, myAccLayout, orderLayout, promoLayout, aboutLayout, help_layout;
    private RelativeLayout subhelp_layout;
    private CTextView txt_faq, txt_contact;
    private ImageView img_left, img_down;

    private View mFragmentContainerView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationDrawerCallBack mCallback;
    private Boolean visible = false;
    private int mCurrentSelectedPosition = HOME;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mDrawerLayout = null;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.drawer_layout, container, false);
        init(view);
        setListener();
        return view;
    }

    private void setListener() {
        homeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        resetBackgrounds();
                        homeLayout.setBackground(getResources().getDrawable(R.drawable.dull_button));
                        selectItem(HOME);
                        mDrawerLayout.closeDrawer(mFragmentContainerView);
                    }
                }, 300);
            }
        });
        myAccLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        resetBackgrounds();
                        myAccLayout.setBackground(getResources().getDrawable(R.drawable.dull_button));
                        selectItem(MY_ACCOUNT);
                        mDrawerLayout.closeDrawer(mFragmentContainerView);
                    }
                }, 300);
            }
        });
        orderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        resetBackgrounds();
                        orderLayout.setBackground(getResources().getDrawable(R.drawable.dull_button));
                        selectItem(ORDER_HISTORY);
                        mDrawerLayout.closeDrawer(mFragmentContainerView);
                    }
                }, 300);
            }
        });
        promoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        resetBackgrounds();
                        promoLayout.setBackground(getResources().getDrawable(R.drawable.dull_button));
                        selectItem(PROMOTION);
                        mDrawerLayout.closeDrawer(mFragmentContainerView);
                    }
                }, 300);
            }
        });
        aboutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        resetBackgrounds();
                        aboutLayout.setBackground(getResources().getDrawable(R.drawable.dull_button));
                        selectItem(ABOUT);
                        mDrawerLayout.closeDrawer(mFragmentContainerView);
                    }
                }, 300);
            }
        });

        txt_faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        resetBackgrounds();
                        txt_faq.setBackground(getResources().getDrawable(R.drawable.dull_button));
                        selectItem(FAQ);
                        mDrawerLayout.closeDrawer(mFragmentContainerView);
                    }
                }, 300);


            }
        });

        txt_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        resetBackgrounds();
                        txt_contact.setBackground(getResources().getDrawable(R.drawable.dull_button));
                        selectItem(CONTACT);
                        mDrawerLayout.closeDrawer(mFragmentContainerView);
                    }
                }, 300);
            }
        });
        img_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subhelp_layout.setVisibility(View.VISIBLE);
                img_down.setVisibility(View.VISIBLE);
                img_left.setVisibility(View.GONE);
            }
        });

        img_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subhelp_layout.setVisibility(View.GONE);
                img_left.setVisibility(View.VISIBLE);
                img_down.setVisibility(View.GONE);
            }
        });

        help_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!visible) {
                    subhelp_layout.setVisibility(View.VISIBLE);
                    img_down.setVisibility(View.VISIBLE);
                    img_left.setVisibility(View.GONE);
                    visible = true;
                } else {
                    subhelp_layout.setVisibility(View.GONE);
                    img_left.setVisibility(View.VISIBLE);
                    img_down.setVisibility(View.GONE);
                    visible = false;
                }
            }
        });
    }

    /**
     * Resests Background of all Linear Layouts to normal color
     */
    private void resetBackgrounds() {
        homeLayout.setBackground(getResources().getDrawable(R.drawable.button_effect));
        myAccLayout.setBackground(getResources().getDrawable(R.drawable.button_effect));
        orderLayout.setBackground(getResources().getDrawable(R.drawable.button_effect));
        promoLayout.setBackground(getResources().getDrawable(R.drawable.button_effect));
        aboutLayout.setBackground(getResources().getDrawable(R.drawable.button_effect));
        help_layout.setBackground(getResources().getDrawable(R.drawable.button_effect));
        txt_faq.setBackground(getResources().getDrawable(R.drawable.button_effect));
        txt_contact.setBackground(getResources().getDrawable(R.drawable.button_effect));
    }

    private void init(View view) {
        help_layout = (LinearLayout) view.findViewById(R.id.help_layout);
        homeLayout = (LinearLayout) view.findViewById(R.id.homeLayout);
        orderLayout = (LinearLayout) view.findViewById(R.id.orderLayout);
        promoLayout = (LinearLayout) view.findViewById(R.id.promoLayout);
        aboutLayout = (LinearLayout) view.findViewById(R.id.aboutLayout);
        myAccLayout = (LinearLayout) view.findViewById(R.id.myAccLayout);
        subhelp_layout = (RelativeLayout) view.findViewById(R.id.subhelp_layout);
        img_down = (ImageView) view.findViewById(R.id.img_down);
        img_left = (ImageView) view.findViewById(R.id.img_left);
        txt_contact = (CTextView) view.findViewById(R.id.txt_contact);
        txt_faq = (CTextView) view.findViewById(R.id.txt_faq);
    }

    public void initDrawer(DrawerLayout drawerLayout, final Toolbar toolbar, int position) {
        mFragmentContainerView = getActivity().findViewById(R.id.navigation_drawer);
        mDrawerLayout = drawerLayout;


        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),
                mDrawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                toolbar.setTitle(R.string.app_name);
                getActivity().supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().supportInvalidateOptionsMenu();
                //Global.keyboardHide(getActivity());
            }
        };
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        //mDrawerToggle.setDrawerIndicatorEnabled(true);
        //mDrawerToggle.setHomeAsUpIndicator(R.drawable.menu);
        mCurrentSelectedPosition = position;
        selectItem(mCurrentSelectedPosition);
       /* if (isDrawerOpen()) {
            getActivity().supportInvalidateOptionsMenu();
            mDrawerToggle.syncState();
        }*/
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mCallback != null) {
            mCallback.onNavigationItemSelected(position);

        }
    }

    public boolean isDrawerOpen() {
        Global.keyboardHide(getActivity());
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.global, menu);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (NavigationDrawerCallBack) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (isDrawerOpen()) {

            mDrawerLayout.closeDrawer(Gravity.LEFT);


        } else if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public interface NavigationDrawerCallBack {
        void onNavigationItemSelected(int position);
    }


}
