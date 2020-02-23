package nl.daanvanberkel.schiphol;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import nl.daanvanberkel.schiphol.helpers.FlightParser;
import nl.daanvanberkel.schiphol.models.Flight;

public class FlightAdapter extends PagedListAdapter<Flight, FlightAdapter.FlightViewHolder> {

    private OnItemClickListener listener;

    protected FlightAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Flight> DIFF_CALLBACK = new DiffUtil.ItemCallback<Flight>() {
        @Override
        public boolean areItemsTheSame(@NonNull Flight oldItem, @NonNull Flight newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Flight oldItem, @NonNull Flight newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getScheduleDate().equals(newItem.getScheduleDate()) &&
                    oldItem.getScheduleTime().equals(newItem.getScheduleTime()) &&
                    oldItem.getGate().equals(newItem.getGate()) &&
                    oldItem.getTerminal() == newItem.getTerminal() &&
                    Arrays.equals(oldItem.getFlightStates(), newItem.getFlightStates()) &&
                    Arrays.equals(oldItem.getDestinations(), newItem.getDestinations());
        }
    };

    @NonNull
    @Override
    public FlightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.flight_list_item, parent, false);

        return new FlightViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FlightViewHolder holder, int position) {
        Flight currentFlight = getItem(position);

        if (currentFlight == null) {
            return;
        }

        // Set text color to red on gate change, otherwise set back to normal color
        if (currentFlight.hasState("GCH")) {
            int color = holder.flightGateView.getContext().getResources().getColor(android.R.color.holo_red_light, holder.flightGateView.getContext().getTheme());
            holder.flightGateView.setTextColor(color);
        } else {
            holder.flightGateView.setTextColor(holder.flightNameView.getTextColors());
        }

        // Set text color to red on delayed, otherwise set back to normal color
        if (currentFlight.getDelayedInMinutes() > 0) {
            int color = holder.flightDateTimeView.getContext().getResources().getColor(android.R.color.holo_red_light, holder.flightDateTimeView.getContext().getTheme());
            holder.flightDateTimeView.setTextColor(color);
        } else {
            holder.flightDateTimeView.setTextColor(holder.flightNameView.getTextColors());
        }

        if (currentFlight.getEstimatedDateTime() != null) {
            holder.flightDateTimeView.setText(currentFlight.getEstimatedDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
        } else {
            holder.flightDateTimeView.setText(currentFlight.getScheduleDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
        }
        holder.flightNameView.setText(currentFlight.getName());
        holder.flightStateView.setText(FlightParser.parseState(currentFlight.getFirstFlightState()));
        holder.flightGateView.setText(currentFlight.getGate());
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Flight flight);
    }

    class FlightViewHolder extends RecyclerView.ViewHolder {

        TextView flightNameView;
        TextView flightDateTimeView;
        TextView flightStateView;
        TextView flightGateView;

        public FlightViewHolder(@NonNull View itemView) {
            super(itemView);

            flightNameView = itemView.findViewById(R.id.flight_item_name);
            flightDateTimeView = itemView.findViewById(R.id.flight_item_datetime);
            flightStateView = itemView.findViewById(R.id.flight_item_state);
            flightGateView = itemView.findViewById(R.id.flight_item_gate);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getItem(position));
                }
            });
        }
    }

}
