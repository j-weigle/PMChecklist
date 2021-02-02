package com.pmchecklist.model;

import android.content.res.Resources;

import com.pmchecklist.R;

public class ChecklistContents {
    private String[][][] electricList;
    private String[][][] combustionList;
    Resources resources;

    public ChecklistContents(Resources resources) {
        this.resources = resources;
        initLists();
    }

    private void initLists() {
        electricList = new String[][][]{
                {
                        {"Walk Around Inspection"},
                        {
                                "Sheet Metal / Frame",
                                "Tires / Wheels / Lugs",
                                "Leaks",
                                "Mast / Chains / Sheaves / Anchors",
                                "Carriage / Rollers / Slides",
                                "Cylinders / Lines / Hoses",
                                "Steer Wheel / Levers / Controls",
                                "Improper Modifications",
                                "Seat / Armrest"
                        }
                },
                {
                        {"Functional Tests"},
                        {
                                "Key / On|Off Switch",
                                "Instrument Panel / Gauges / Lights",
                                "Service / Park Brake",
                                "Fwd|Rev Control",
                                "Steering",
                                "Hydraulic Functions",
                                "Unusual Noises",
                                "Lift|Tilt Functions",
                                "Side-shift / Attachment Functions"
                        }
                },
                {
                        {"Clean With Air + Wipe Truck Down"},
                        {
                                "Main Truck",
                                "Electrical Compartments",
                                "Motors"
                        }
                },
                {
                        {"Lube and Fill"},
                        {
                                "Hydraulic Oil",
                                "Differential Oil",
                                "Brake Fluid",
                                "Mast Mounts",
                                "Tilt Mounts",
                                "Steer Links",
                                "Attachments"
                        }
                },
                {
                        {resources.getString(R.string.checklist_safety_header)},
                        {
                                "Driver Overhead Guard / Cab",
                                "Load Back Rest",
                                "Horn",
                                "Backup Alarm",
                                "Blue / Strobe Light",
                                "Fire Extinguisher",
                                "Seat Belt",
                                "Counter Weight Bolt"
                        }
                },
                {
                        {"Basic Battery Checks"},
                        {
                                "Corrosion",
                                "Cables",
                                "Connections",
                                "Cell Caps",
                                "Cell Link Covers"
                        }
                },
                {
                        {"Electrical Visual Checks"},
                        {
                                "Drive Motors",
                                "Hydraulic Motors",
                                "Steer Motor",
                                "Contactors",
                                "Cables",
                                "Wiring",
                                "Switches",
                                "Sensors"
                        }
                },
                {
                        {"Recommended/Additional Services"},
                        {
                                "Differential Oil",
                                "Hydraulic Oil and Filter"
                        }
                }
        };

        combustionList = new String[][][]{
                {
                        {"Walk Around Inspection"},
                        {
                                "Sheet Metal / Frame",
                                "Tires / Wheels / Lugs",
                                "Leaks",
                                "Mast / Chains / Sheaves / Anchors",
                                "Carriage / Rollers / Slides",
                                "Cylinders / Lines / Hoses",
                                "Steer Wheel / Levers / Controls",
                                "Improper Modifications",
                                "Seat / Armrest"
                        }
                },

                {
                        {"Functional Tests"},
                        {
                                "Key / On|Off Switch",
                                "Instrument Panel / Gauges / Lights",
                                "Service / Park Brake",
                                "Fwd|Rev Control",
                                "Steering",
                                "Hydraulic Functions",
                                "Unusual Noises",
                                "Lift|Tilt Functions",
                                "Side-shift / Attachment Functions"
                        }
                },

                {
                        {"Clean With Air + Wipe Truck Down"},
                        {
                                "Main Truck",
                                "Engine Compartment",
                                "Radiator"
                        }
                },

                {
                        {"Lube and Fill"},
                        {
                                "Hydraulic Oil",
                                "Differential Oil",
                                "Brake Fluid",
                                "Mast Mounts",
                                "Tilt Mounts",
                                "Steer Links",
                                "Attachments"
                        }
                },

                {
                        {resources.getString(R.string.checklist_safety_header)},
                        {
                                "Driver Overhead Guard / Cab",
                                "Load Back Rest",
                                "Horn",
                                "Backup Alarm",
                                "Blue / Strobe Light",
                                "Fire Extinguisher",
                                "Seat Belt",
                                "Counter Weight Bolt"
                        }
                },

                {
                        {"Internal Combustion Checks"},
                        {
                                "Wire Connections and Condition",
                                "Change Engine Oil and Filter",
                                "Check and Clean P.C.V. Valve",
                                "Check Air Filter / Clean Canister",
                                "Intake Hoses / Elbows / Clamps",
                                "Belts - Condition and Tension",
                                "Exhaust Pipe and Muffler",
                                "Clean Fuel Filters or Replace",
                                "Fuel System and Line Leaks",
                                "Carburetor Check Idle",
                                "Diesel Check Cold Start/Glow Plugs",
                                "Diesel Clean Water Trap",
                                "Check Fuel Lock Off",
                                "Clean Battery Cables and Terminals"
                        }
                },

                {
                        {"Recommended/Additional Services"},
                        {
                                "Transmission Oil Filter",
                                "Transmission Oil",
                                "Differential Oil",
                                "Air Filter",
                                "Engine Coolant",
                                "Hydraulic Oil and Filter",
                                "Spark Plugs / Cap / Rotor"
                        }
                }
        };
    }

    public String[][][] getContents(String type) {
        if (type.equals("Electric Checklist")) {
            return electricList;
        } else if (type.equals("Combustion Checklist")) {
            return combustionList;
        } else {
            return null;
        }
    }
}
