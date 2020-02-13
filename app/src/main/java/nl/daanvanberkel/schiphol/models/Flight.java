package nl.daanvanberkel.schiphol.models;

import java.io.Serializable;

public class Flight implements Serializable {
    /**
     * {
     *   "flights": [
     *     {
     *       "lastUpdatedAt": "2020-02-11T09:04:21.190Z",
     *       "actualLandingTime": "2020-02-11T09:04:21.190Z",
     *       "actualOffBlockTime": "2020-02-11T09:04:21.190Z",
     *       "aircraftRegistration": "string",
     *       "aircraftType": {                                      --> aircraftType
     *         "iataMain": "string",
     *         "iataSub": "string"
     *       },
     *       "baggageClaim": {
     *         "belts": [
     *           "string"
     *         ]
     *       },
     *       "checkinAllocations": {
     *         "checkinAllocations": [
     *           {
     *             "endTime": "2020-02-11T09:04:21.190Z",
     *             "rows": {
     *               "rows": [
     *                 {
     *                   "position": "string",
     *                   "desks": {
     *                     "desks": [
     *                       {
     *                         "checkinClass": {
     *                           "code": "string",
     *                           "description": "string"
     *                         },
     *                         "position": 0
     *                       }
     *                     ]
     *                   }
     *                 }
     *               ]
     *             },
     *             "startTime": "2020-02-11T09:04:21.190Z"
     *           }
     *         ],
     *         "remarks": {
     *           "remarks": [
     *             "string"
     *           ]
     *         }
     *       },
     *       "codeshares": {                                            --> codeshares
     *         "codeshares": [
     *           "string"
     *         ]
     *       },
     *       "estimatedLandingTime": "2020-02-11T09:04:21.190Z",
     *       "expectedTimeBoarding": "2020-02-11T09:04:21.190Z",
     *       "expectedTimeGateClosing": "2020-02-11T09:04:21.190Z",
     *       "expectedTimeGateOpen": "2020-02-11T09:04:21.190Z",
     *       "expectedTimeOnBelt": "2020-02-11T09:04:21.190Z",
     *       "expectedSecurityFilter": "string",
     *       "flightDirection": "A",
     *       "flightName": "string",                                    --> name
     *       "flightNumber": 0,
     *       "gate": "string",                                          --> gate
     *       "pier": "string",
     *       "id": "string",                                            --> id
     *       "mainFlight": "string",
     *       "prefixIATA": "string",
     *       "prefixICAO": "string",
     *       "airlineCode": 0,
     *       "publicEstimatedOffBlockTime": "2020-02-11T09:04:21.190Z",
     *       "publicFlightState": {                                     --> flightStates
     *         "flightStates": [
     *           "string"
     *         ]
     *       },
     *       "route": {                                                 --> destinations
     *         "destinations": [
     *           "string"
     *         ],
     *         "eu": "string",
     *         "visa": true
     *       },
     *       "scheduleDateTime": "2020-02-11T09:04:21.190Z",
     *       "scheduleDate": "string",                                  --> scheduleDate
     *       "scheduleTime": "string",                                  --> scheduleTime
     *       "serviceType": "string",
     *       "terminal": 0,                                             --> terminal
     *       "transferPositions": {
     *         "transferPositions": [
     *           0
     *         ]
     *       },
     *       "schemaVersion": "string"
     *     }
     *   ]
     * }
     */

    private String id;
    private String scheduleDate;
    private String scheduleTime;
    private String[] flightStates;
    private String name;
    private String[] destinations = {};
    private int terminal = 0;
    private String gate;
    private AircraftType aircraftType;
    private String mainFlight;
    private String[] codeShares;
    private String icao;

    public void setId(String id) {
        this.id = id;
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public void setScheduleTime(String scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public void setFlightStates(String[] flightStates) {
        this.flightStates = flightStates;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDestinations(String[] destinations) {
        this.destinations = destinations;
    }

    public void setTerminal(int terminal) {
        this.terminal = terminal;
    }

    public void setGate(String gate) {
        this.gate = gate;
    }

    public void setAircraftType(AircraftType aircraftType) {
        this.aircraftType = aircraftType;
    }

    public void setMainFlight(String mainFlight) {
        this.mainFlight = mainFlight;
    }

    public void setCodeShares(String[] codeShares) {
        this.codeShares = codeShares;
    }

    public void setIcao(String icao) {
        this.icao = icao;
    }

    public String getId() {
        return id;
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    public String getScheduleTime() {
        return scheduleTime;
    }

    public String[] getFlightStates() {
        return flightStates;
    }

    public String getName() {
        return name;
    }

    public String[] getDestinations() {
        return destinations;
    }

    public int getTerminal() {
        return terminal;
    }

    public String getGate() {
        return gate;
    }

    public AircraftType getAircraftType() {
        return aircraftType;
    }

    public String getMainFlight() {
        return mainFlight;
    }

    public String[] getCodeShares() {
        return codeShares;
    }

    public String getIcao() {
        return icao;
    }

    public String getFirstFlightState() {
        if (getFlightStates().length > 0) {
            return getFlightStates()[0];
        } else {
            return "State unknown";
        }
    }

    public boolean hasState(String state) {
        if (getFlightStates() != null && getFlightStates().length > 0) {
            for (String s: getFlightStates()) {
                if (s.equals(state)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static class AircraftType implements Serializable {
        private String iataMain;
        private String iataSub;

        public void setIataMain(String iataMain) {
            this.iataMain = iataMain;
        }

        public void setIataSub(String iataSub) {
            this.iataSub = iataSub;
        }

        public String getIataMain() {
            return iataMain;
        }

        public String getIataSub() {
            return iataSub;
        }
    }
}
