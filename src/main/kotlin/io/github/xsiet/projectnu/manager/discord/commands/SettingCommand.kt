package io.github.xsiet.projectnu.manager.discord.commands

import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData

val settingCommand = Commands.slash("설정", "설정 명령어")
    .setGuildOnly(true)
    .addSubcommandGroups(
        SubcommandGroupData("디스코드", "디스코드 설정 명령어")
            .addSubcommands(
                SubcommandData("연동서버", "이 디스코드 서버를 마인크래프트 서버와 연동합니다"),
                SubcommandData("공지채널", "디스코드 서버의 공지 채널을 설정합니다")
                    .addOption(OptionType.CHANNEL, "채널", "공지 채널로 설정할 채널을 입력해 주세요!", true),
                SubcommandData("광장채널", "디스코드 서버의 광장 채널을 설정합니다")
                    .addOption(OptionType.CHANNEL, "채널", "광장 채널로 설정할 채널을 입력해 주세요!", true)
            ),
        SubcommandGroupData("서버접속", "서버 접속 설정 명령어")
            .addSubcommands(
                SubcommandData("허용", "모든 플레이어의 서버 접속을 허용합니다"),
                SubcommandData("차단", "모든 플레이어의 서버 접속을 차단합니다")
            ),
        SubcommandGroupData("파밍시간", "파밍 시간 설정 명령어")
            .addSubcommands(
                SubcommandData("활성화", "파밍 시간을 활성화합니다"),
                SubcommandData("비활성화", "파밍 시간을 비활성화합니다")
            )
    )