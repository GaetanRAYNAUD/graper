package fr.graynaud.discord.graper.service.chart;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.GuildChannel;
import fr.graynaud.discord.graper.service.discord.commands.TextPercentCommand;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChartUtils {

    public static String getPieRecap(Map<String, Long> data, long total, Collection<GuildChannel> channels) {
        return getPieRecap(data.entrySet()
                               .stream()
                               .collect(Collectors.toMap(
                                       e -> channels.stream()
                                                    .filter(m -> String.valueOf(m.getId().asLong()).equals(e.getKey()))
                                                    .findFirst()
                                                    .map(GuildChannel::getName)
                                                    .orElse(e.getKey()),
                                       Map.Entry::getValue)), total);
    }

    public static String getPieRecap(Map<String, Long> data, long total, List<Member> members) {
        return getPieRecap(data.entrySet()
                               .stream()
                               .collect(Collectors.toMap(
                                       e -> members.stream()
                                                   .filter(m -> String.valueOf(m.getId().asLong()).equals(e.getKey()))
                                                   .findFirst()
                                                   .map(m -> m.getNickname()
                                                              .orElse(m.getMemberData().user().globalName().orElse(m.getMemberData().user().username())))
                                                   .orElse(e.getKey()),
                                       Map.Entry::getValue)), total);
    }

    public static String getPieRecap(Map<String, Long> data, long total) {
        return getPie(Stream.concat(data.entrySet().stream(), Stream.of(Pair.of("Autres", total - data.values().stream().mapToLong(Long::longValue).sum())))
                            .filter(e -> e.getValue() > 0)
                            .sorted(Map.Entry.comparingByValue())
                            .collect(Collectors.toMap(
                                    e -> e.getKey() + " (" + e.getValue() + " - " + TextPercentCommand.NUMBER_FORMAT.format(e.getValue() / (double) total) +
                                         ")", Map.Entry::getValue, (a, b) -> a, TreeMap::new)));
    }

    public static String getPie(Map<String, Long> data) {
        String sb = "https://image-charts.com/chart?cht=p&chs=800x450&chd=a:" + data.values().stream().map(String::valueOf).collect(Collectors.joining(",")) +
                    "&chl=" + data.keySet().stream().map(String::valueOf).collect(Collectors.joining("|"));

        return UriComponentsBuilder.fromUriString(sb).toUriString();
    }

    public static String getBHS(Map<String, Long> data, long total) {
        return getBHS(Stream.concat(data.entrySet().stream(), Stream.of(Pair.of("Autres", total - data.values().stream().mapToLong(Long::longValue).sum())))
                            .filter(e -> e.getValue() > 0)
                            .collect(Collectors.toMap(
                                    e -> e.getKey() + " (" + e.getValue() + " - " + TextPercentCommand.NUMBER_FORMAT.format(e.getValue() / (double) total) +
                                         ")", Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new)));
    }

    public static String getBHS(Map<String, Long> data) {
        String sb = "https://image-charts.com/chart?cht=bhs&chs=800x450&chdlp=b&chd=a:" +
                    data.values().stream().map(String::valueOf).collect(Collectors.joining("|")) +
                    "&chdl=" + data.keySet().stream().map(String::valueOf).collect(Collectors.joining("|")) +
                    "&chl=" + data.values().stream().map(String::valueOf).collect(Collectors.joining("|"));

        return UriComponentsBuilder.fromUriString(sb).toUriString();
    }

    public static String getBVG(Map<String, Long> data, long total) {
        return getBVG(Stream.concat(data.entrySet().stream(), Stream.of(Pair.of("Autres", total - data.values().stream().mapToLong(Long::longValue).sum())))
                            .filter(e -> e.getValue() > 0)
                            .collect(Collectors.toMap(
                                    e -> e.getKey() + " (" + e.getValue() + " - " + TextPercentCommand.NUMBER_FORMAT.format(e.getValue() / (double) total) +
                                         ")", Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new)));
    }

    public static String getBVG(Map<String, Long> data) {
        String sb = "https://image-charts.com/chart?cht=bvg&chs=800x450&chdlp=b&chd=a:" +
                    data.values().stream().map(String::valueOf).collect(Collectors.joining("|")) +
                    "&chdl=" + data.keySet().stream().map(String::valueOf).collect(Collectors.joining("|")) +
                    "&chl=" + data.values().stream().map(String::valueOf).collect(Collectors.joining("|"));

        return UriComponentsBuilder.fromUriString(sb).toUriString();
    }
}
