package se.mbaeumer.fxlink.api;

import se.mbaeumer.fxlink.handlers.FollowUpStatusReadDBHandler;
import se.mbaeumer.fxlink.handlers.GenericDBHandler;
import se.mbaeumer.fxlink.models.FollowUpOption;
import se.mbaeumer.fxlink.models.FollowUpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FollowUpOptionHandler {

    private final FollowUpStatusReadDBHandler followUpStatusReadDBHandler;

    public FollowUpOptionHandler(FollowUpStatusReadDBHandler followUpStatusReadDBHandler) {
        this.followUpStatusReadDBHandler = followUpStatusReadDBHandler;
    }

    public List<FollowUpOption> getFollowUpOptions(){
        List<FollowUpOption> followUpOptions = new ArrayList<>();

        followUpOptions.add(new FollowUpOption("ALL"));
        followUpOptions.add(new FollowUpOption("NEXT"));
        final List<FollowUpStatus> followUpStatuses = followUpStatusReadDBHandler.getFollowUpStatuses(GenericDBHandler.getInstance());

        followUpOptions.addAll(followUpStatuses
                .stream()
                .map(s -> FollowUpOption.of(s))
                .collect(Collectors.toList()));

        return followUpOptions;
    }
}
