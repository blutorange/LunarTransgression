.headers on
.mode csv

-- Utils

-- All pokemons with name and id
.output dexnumber_name.csv
select p.species_id,p.identifier
    from pokemon as p
    where p.identifier not like '%-%'
        or p.identifier like '%-f'
        or p.identifier like '%-m'
        or p.identifier in ('wormadam-trash', 'deoxys-normal', 'ho-oh',
            'mr-mime', 'mime-jr', 'porygon-z', 'giratina-origin', 'shaymin-sky',
            'darmanitan-zen', 'basculin-red-striped', 'tornadus-incarnate',
            'thundurus-incarnate', 'landorus-incarnate', 'keldeo-resolute',
            'meloetta-pirouette','meowstic-female','aegislash-shield',
            'pumpkaboo-average','gourgeist-average')
    order by p.species_id;

--Non-damage skills
.output status_skills.csv
select replace(upper(m.identifier),'-','_') as effect
    from moves as m
    join move_damage_classes as mdc on m.damage_class_id=mdc.id
    where mdc.identifier='status'
    order by m.damage_class_id,m.identifier
;

-- Character
.output output/character.csv
select 
        upper(substr(names.name,1,1)) || substr(names.name,2) as name,
        (substr('000'||p.species_id,-3,3) || '.json') as imgFront,
        (substr('000'||p.species_id,-3,3) || 'b.json') as imgBack,
        p.species_id || '.png' as imgIcon,
        (p.species_id || '.webm') as cry,
        upper(replace(case gr.identifier
            when 'slow-then-very-fast' then 'erratic'
            when 'fast-then-very-slow' then 'fluctuating'
            when 'medium' then 'medium-fast'
            else gr.identifier end,'-','_')) as experienceGroup,
        hp.base_stat as maxHp,
        hp.base_stat as maxMp,
        upper(elements.list) as elements,
        100 as accuracy,
        100 as evasion,
        attack.base_stat as physicalAttack,
        defense.base_stat as physicalDefense,
        sattack.base_stat as magicalAttack,
        sdefense.base_stat as magicalDefense,
        speed.base_stat as speed,
        replace(dex.flavor_text,char(10),' ') as description
    from pokemon as p
    join (select id,
            case
                when identifier = 'nidoran-f' then 'nidoran (female)'
                when identifier = 'nidoran-m' then 'nidoran (male)'
                when instr(identifier, '-') > 0 then substr(identifier, 0, instr(identifier, '-'))
                else identifier end as name
            from pokemon)
        as names on p.id = names.id
    join (select pt.pokemon_id,group_concat(t.identifier) as list from pokemon_types as pt
            join types as t on pt.type_id=t.id group by pt.pokemon_id)
        as elements on elements.pokemon_id = p.id
    join (select ps.pokemon_id,ps.base_stat from pokemon_stats as ps join stats as s on ps.stat_id=s.id where s.identifier='hp')
        as hp on hp.pokemon_id = p.id
    join (select ps.pokemon_id,ps.base_stat from pokemon_stats as ps join stats as s on ps.stat_id=s.id where s.identifier='attack')
        as attack on attack.pokemon_id = p.id
    join (select ps.pokemon_id,ps.base_stat from pokemon_stats as ps join stats as s on ps.stat_id=s.id where s.identifier='defense')
        as defense on defense.pokemon_id = p.id
    join (select ps.pokemon_id,ps.base_stat from pokemon_stats as ps join stats as s on ps.stat_id=s.id where s.identifier='special-attack')
        as sattack on sattack.pokemon_id = p.id
    join (select ps.pokemon_id,ps.base_stat from pokemon_stats as ps join stats as s on ps.stat_id=s.id where s.identifier='special-defense')
        as sdefense on sdefense.pokemon_id = p.id
    join (select ps.pokemon_id,ps.base_stat from pokemon_stats as ps join stats as s on ps.stat_id=s.id where s.identifier='speed')
        as speed on speed.pokemon_id = p.id
    join pokemon_species as ps on p.species_id=ps.id
    join growth_rates as gr on ps.growth_rate_id=gr.id
    join (select t.species_id, t.flavor_text from pokemon_species_flavor_text as t
            join languages as l on t.language_id=l.id
            join versions as v on t.version_id=v.id
            where l.iso639 = 'en' and v.identifier='x')
        as dex on dex.species_id = p.species_id
    where p.identifier not like '%-%'
        or p.identifier like '%-f'
        or p.identifier like '%-m'
        or p.identifier in ('wormadam-trash', 'deoxys-normal', 'ho-oh',
            'mr-mime', 'mime-jr', 'porygon-z', 'giratina-origin', 'shaymin-sky',
            'darmanitan-zen', 'basculin-red-striped', 'tornadus-incarnate',
            'thundurus-incarnate', 'landorus-incarnate', 'keldeo-resolute',
            'meloetta-pirouette','meowstic-female','aegislash-shield',
            'pumpkaboo-average','gourgeist-average')
    order by p.species_id
;

-- Skill
.output output/skill.csv
select
        replace(upper(substr(m.identifier,1,1)) || substr(m.identifier,2),'-',' ') as name,
        100/m.pp as mp,
        ifnull(m.power,0) as attackPower,
        ifnull(mm.healing,0) as healPower,
        (select replace(group_concat(upper(
                    case s.identifier
                    when 'attack' then 'physical-attack'
                    when 'defense' then 'physical-defense'
                    when 'special-attack' then 'magical-attack'
                    when 'special-defense' then 'magical-defense'
                    else s.identifier end), ','), '-', '_')
                || ':'
                || group_concat(mmsc1.change,',')
            from moves as m1
            left outer join move_meta_stat_changes as mmsc1 on mmsc1.move_id = m1.id
            join stats as s on mmsc1.stat_id = s.id
            where m1.id = m.id
            group by mmsc1.change) as stageChanges,
        ifnull(m.accuracy,100) as accuracy,
        ifnull(m.priority,0) as priority,
        case when m.accuracy is null then 'true' else 'false' end as alwaysHits,
        upper(t.identifier) as element,
        replace(upper(mt.identifier),'-','_') as target,
        case mm.crit_rate when 0 then 'false' else 'true' end as highCritical,
        case mdc.identifier when 'special' then 'false' else 'true' end as isPhysical,
        upper(replace(replace(mmc.identifier,'+','_'),'-','_')) as effect,
        upper(replace(m.identifier,'-','_')) as flavor,
	    case when mma.identifier in ('paralysis', 'sleep', 'freeze', 'burn', 'poison', 'confusion')
            then upper(mma.identifier)
            else null end as condition,
    	ifnull(mm.ailment_chance,0) as conditionChance,
        ifnull(mm.flinch_chance,0) as flinchChance,
    	ifnull(mm.stat_chance,0) as stageChance,
        replace(mep.short_effect,char(10),' ') as description
    from moves as m
    join move_damage_classes as mdc on m.damage_class_id=mdc.id
    join types as t on m.type_id=t.id
    join move_effect_prose as mep on mep.move_effect_id=m.effect_id
    join languages as mepl on mep.local_language_id=mepl.id
    join move_targets as mt on m.target_id=mt.id
    join move_meta as mm on mm.move_id=m.id
    join move_meta_ailments as mma on mm.meta_ailment_id = mma.id
    join move_meta_categories as mmc on mm.meta_category_id = mmc.id
    where mepl.iso639 = 'en' and m.identifier != 'struggle'
    order by mmc.identifier,m.identifier
;

-- Character to Skill
.output output/character_skill.csv
select
        upper(substr(names.name,1,1)) || substr(names.name,2) as characterId,
        replace(upper(substr(m.identifier,1,1)) || substr(m.identifier,2),'-',' ') as skillId,
        pm.'level' as 'level'
    from pokemon_moves as pm
    join pokemon as p on pm.pokemon_id=p.id
    join (select id,
            case
                when identifier = 'nidoran-f' then 'nidoran (female)'
                when identifier = 'nidoran-m' then 'nidoran (male)'
                when instr(identifier, '-') > 0 then substr(identifier, 0, instr(identifier, '-'))
                else identifier end as name
            from pokemon)
        as names on p.id = names.id
    join moves as m on pm.move_id=m.id
    join version_groups as v on pm.version_group_id=v.id
    join pokemon_move_methods as pmm on pm.pokemon_move_method_id=pmm.id
    where 
        v.identifier='x-y'
        and pmm.identifier in ('level-up', 'egg')
        and (p.identifier not like '%-%'
            or p.identifier like '%-f'
            or p.identifier like '%-m'
            or p.identifier in ('wormadam-trash', 'deoxys-normal', 'ho-oh',
                'mr-mime', 'mime-jr', 'porygon-z', 'giratina-origin', 'shaymin-sky',
                'darmanitan-zen', 'basculin-red-striped', 'tornadus-incarnate',
                'thundurus-incarnate', 'landorus-incarnate', 'keldeo-resolute',
                'meloetta-pirouette','meowstic-female','aegislash-shield',
                'pumpkaboo-average','gourgeist-average'))
    order by names.name,pm.'level',m.identifier
;

-- reset output
.output
